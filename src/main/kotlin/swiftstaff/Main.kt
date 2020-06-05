package swiftstaff

import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import org.litote.kmongo.eq
import swiftstaff.api.v1.*
import io.github.rybalkinsd.kohttp.dsl.httpPost
import io.github.rybalkinsd.kohttp.ext.url
import org.apache.log4j.BasicConfigurator

// Main server
fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(CORS) {
        anyHost()
        method(HttpMethod.Options)
        allowNonSimpleContentTypes = true
    }

    install(WebSockets)
    // JSON / GSON conversion
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
        }
    }


    routing {

        post("/api/v1/signup/worker") {
            val signup = call.receive<SignupWorker>()
            println("Worker Signup correctly recieved")
            val worker = Worker(
                fName = signup.fName,
                lName = signup.lName,
                phone = signup.phone,
                dob = signup.dob
            )
            println("Worker class created")

            val success = MongoDatabase.insert(worker)
            println("Worker inserted success: $success")

            if (success) {
                println("About to create user")
                val user = createUser(signup, worker, UserType.Worker)
                println("User Created")
                val success = MongoDatabase.insert(user)
                println("User succesfully inserted")

                if (success){
                    println("About to respond")

                    call.respond(status = HttpStatusCode.Created, message = mapOf("id" to user._id))
                    println("Responding")

                } else {
                    println("User unsuccesful")

                    internalServerError(call = call)
                }
            } else {
                println("worker unsucessful")

                internalServerError(call = call)
            }
        }

        post("/api/v1/signup/restaurant") {
            val signup = call.receive<SignupRestaurant>()
            val restaurant = Restaurant(
                name = signup.name,
                phone = signup.phone,
                restaurantEmailAddress = signup.restaurantEmailAddress,
                address = signup.address
            )
            val success = MongoDatabase.insert(restaurant)
            if (success) {
                val user = createUser(signup, restaurant, UserType.Restaurant)
                val success = MongoDatabase.insert(user)
                if (success){
                    call.respond(status = HttpStatusCode.Created, message = mapOf("id" to user._id))
                } else {
                    call.respond(message = "Internal Server Error", status = HttpStatusCode.InternalServerError)
                }
            } else {
                call.respond(message = "Internal Server Error", status = HttpStatusCode.InternalServerError)
            }
        }

        get("/api/v1/jobs") {
            val jobs = MongoDatabase.find<Job>()
            val jobsList: MutableList<JobResponse> = mutableListOf()
            jobs.forEach {
                val restaurant = MongoDatabase.find<Restaurant>(Restaurant::_id eq it.restaurantId)
                val jobResponse =  if(restaurant.isNotEmpty()){
                    JobResponse(job = it, restaurant = restaurant.first())
                } else {
                    JobResponse(job = it, restaurant = Restaurant())
                }
                jobsList.add(jobResponse)

            }
            if (jobs.isNotEmpty()) {
                call.respond(status = HttpStatusCode.OK, message = Jobs(jobs.size, jobsList))
            }
        }


        post("/api/v1/login") {
            println("enter user")

            val loginAttempt = call.receive<LoginAttempt>()
            println("recieved user")
            val users = MongoDatabase.find<User>(User::email eq loginAttempt.email)
            println("found user")
            println(users.size)
            if (users.isNotEmpty()) {
                val user = users.first();
                val passwordHash = user.passwordHash
                val salt = user.salt
                if (passwordHash == hashPassword(salt, loginAttempt.password)) {
                    println("passwords match")
                    val fcmTokenList = user.fcmTokens;
                    if (!fcmTokenList.contains(loginAttempt.fcmToken)) {
                        fcmTokenList.add(loginAttempt.fcmToken);
                        user.fcmTokens = fcmTokenList
                        MongoDatabase.update(data = user, filter = User::email eq user.email)
                    }
                    println("after tokens")

                    if (user.userType == UserType.Worker.num) {
                        val workerObj = MongoDatabase.find<Worker>(Worker::_id eq user.foreignTableId)
                        if (workerObj.isNotEmpty()) {
                            val worker = workerObj.first()
                            val responseObject = LoginWorkerResponse(userId = user._id.orEmpty(), userType = user.userType, email = user.email, fName = worker.fName,
                                    lName = worker.lName, phone = worker.phone, signUpFinished = user.signUpFinished)
                            call.respond(status = HttpStatusCode.OK, message = responseObject)
                        } else {
                            internalServerError(call = call)
                        }
                    } else if (user.userType == UserType.Restaurant.num) {
                        val restaurantObj = MongoDatabase.find<Restaurant>(Restaurant::_id eq user.foreignTableId)
                        if (restaurantObj.isNotEmpty()) {
                            val restaurant = restaurantObj.first()
                            val responseObject = LoginRestaurantResponse(userId = user._id.orEmpty(), userType = user.userType, email = user.email, fName = "",
                                    lName = "", restaurantPhone = restaurant.phone, restaurantName = restaurant.name,
                                    restaurantEmail = restaurant.restaurantEmailAddress, signUpFinished = user.signUpFinished,
                                    restaurantId = restaurant._id.orEmpty())
                            call.respond(status = HttpStatusCode.OK, message = responseObject)
                        } else {
                            internalServerError(call = call)
                        }
                    } else {
                        internalServerError(call = call)
                    }
                } else {
                    call.respond(message = "Unauthorized", status = HttpStatusCode.Unauthorized)
                }
            } else {
                call.respond(message = "Unauthorized", status = HttpStatusCode.Unauthorized)
            }

        }
        webSocket("/api/v1/jobs") { // websocketSession
            for (frame in incoming) {
                try {
                    // We starts receiving messages (frames).
                    // Since this is a coroutine. This coroutine is suspended until receiving frames.
                    // Once the connection is closed, this consumeEach will finish and the code will continue.
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println(text)
                            val jobs = MongoDatabase.find<Job>()
                            val jobsList: MutableList<JobResponse> = mutableListOf()
                            jobs.forEach {
                                val restaurant = MongoDatabase.find<Restaurant>(Restaurant::_id eq it.restaurantId)
                                val jobResponse =  if(restaurant.isNotEmpty()){
                                   JobResponse(job = it, restaurant = restaurant.first())
                                } else {
                                    JobResponse(job = it, restaurant = Restaurant())
                                }
                                jobsList.add(jobResponse)
                            }
                            if (jobs.isNotEmpty()) {
                                val gson = Gson()
                                outgoing.send(Frame.Text(gson.toJson(Jobs(jobs.size, jobsList))))
                            }

                            if (text.equals("bye", ignoreCase = true)) {
                                close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                            }
                        }
                        else -> {
                        }
                    }
                } finally {
                    // Either if there was an error, of it the connection was closed gracefully.
                    // We notify the server that the member left.
                }
            }
        }


        post("/api/v1/jobs") {
            val newJob = call.receive<NewJobRequest>()
            val job = Job(
                    restaurantId = newJob.restaurantId,
                    sendStrategyId = newJob.sendStrategyId,
                    hourlyRate = newJob.hourlyRate,
                    expertiseId = newJob.expertiseId,
                    startTime = newJob.startTime,
                    endTime = newJob.endTime,
                    date = newJob.date,
                    extraInfo = newJob.extraInfo)
            val success = MongoDatabase.insert(job)
            if (success) {
                call.respond(status = HttpStatusCode.Created, message = mapOf("id" to job._id))
                sendJobsOut(job)
            } else {
                internalServerError(call = call)
            }
        }
    }
}

private suspend fun internalServerError(message:String = "Internal Server Error", call: ApplicationCall){
    return call.respond(message = message, status = HttpStatusCode.InternalServerError)
}

private fun createUser(signup: Credentials, collection: Collection, userType: UserType): User {
    println("About to generate salt")
    val salt: String = generateSalt()
    println("Salt generated")

    val passwordHash: String = hashPassword(salt, signup.password)
    println("Hash generated")
    println("About to return user class")

    return User(
        email = signup.email,
        passwordHash = passwordHash,
        salt = salt,
        userType = userType.num,
        foreignTableId = collection._id!!
    )
}

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    embeddedServer(Netty, 8080, module = Application::module).start()
}



fun sendJobsOut(job: Job){
    val restaurants = MongoDatabase.find<Restaurant>(Restaurant::_id eq job.restaurantId);
    if (restaurants.isNotEmpty()) {
        println("sending")
        val users = MongoDatabase.find<User>()
        val restaurantName = restaurants.first().name
        for (user in users) {
            user.fcmTokens.forEach {
                if(user.userType == UserType.Worker.num) {
                    println(it)
                    sendFirebaseNotification(
                        registrationToken = it,
                        notificationTitle = "New Job Available",
                        notificationMessage = "Please open your app to see new job available at $restaurantName"
                    )
                }
            }
        }

    }
}

fun sendFirebaseNotification(registrationToken: String, notificationTitle: String = "", notificationMessage: String = "", data: Map<String, String>? = null) {

    val key = "key= AAAARV140IQ:APA91bG4khwUnHSpOHOqkOWYNGt0QaOn3-ZVUrXtnI6LgTyZRoakAcM9bNlsGAaVUJ45PrJ5bQbHQCOcgPYUnpAB-fO6bgA7nt0V0lanYvGGORWo-W7zob5rGXdH2-RQOsCeOBOY4GMg"
    val response = httpPost {
        url("https://fcm.googleapis.com/fcm/send")

        header { "Authorization" to key }

        body { json ("""{ 
                                    "notification": {
                                                        "title": "$notificationTitle",
                                                        "text": "$notificationMessage",
                                                    },
                                    "to" : "$registrationToken"
                                }""")
            }
        }

    println("Successfully sent message: $response")

}



