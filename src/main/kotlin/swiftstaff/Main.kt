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
import org.apache.log4j.BasicConfigurator
import org.litote.kmongo.eq
import swiftstaff.api.v1.*


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
            val worker = Worker(
                fName = signup.fName,
                lName = signup.lName,
                phone = signup.phone,
                dob = signup.dob
            )
            val success = MongoDatabase.insert(worker)
            if (success) {
                val user = createUser(signup, worker, UserType.Worker)
                val success = MongoDatabase.insert(user)
                if (success){
                    call.respond(status = HttpStatusCode.Created, message = mapOf("id" to user._id))
                } else {
                    internalServerError(call = call)
                }
            } else {
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
            if (jobs.isNotEmpty()) {
                call.respond(status = HttpStatusCode.OK, message = Jobs(jobs.size, jobs))
            }
        }


        get("/api/v1/login") {
            val loginAttempt: LoginAttempt = call.receive<LoginAttempt>()
            val users = MongoDatabase.find<User>(User::email eq loginAttempt.email)
            if (users.isNotEmpty()) {
                val user = users.first();
                val passwordHash = user.passwordHash
                val salt = user.salt
                if (passwordHash == hashPassword(salt, loginAttempt.password)) {
                    val fcmTokenList = user.fcmTokens;
                    if (!fcmTokenList.contains(loginAttempt.fcmToken)) {
                        fcmTokenList.add(loginAttempt.fcmToken);
                        user.fcmTokens = fcmTokenList
                        MongoDatabase.update(data = user, filter = User::email eq user.email)
                    }

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
                                    lName = "", restaurantPhone = restaurant.phone, restaurantName = restaurant.name, restaurantEmail = restaurant.restaurantEmailAddress, signUpFinished = user.signUpFinished)
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
                            if (jobs.isNotEmpty()) {
                                val gson = Gson()
                                outgoing.send(Frame.Text(gson.toJson(Jobs(jobs.size, jobs))))
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
            val job = call.receive<Job>()
            val success = MongoDatabase.insert(job)
            if (success) {
                call.respond(status = HttpStatusCode.Created, message = mapOf("id" to job._id))
            } else {
                internalServerError(call = call)
            }
        }
    }
}

private suspend fun internalServerError(message:String = "Internal Server Error", call: ApplicationCall){
    return call.respond(message = "Internal Server Error", status = HttpStatusCode.InternalServerError)
}

private fun createUser(signup: Credentials, collection: Collection, userType: UserType): User {
    val salt: String = generateSalt()
    val passwordHash: String = hashPassword(salt, signup.password)
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



