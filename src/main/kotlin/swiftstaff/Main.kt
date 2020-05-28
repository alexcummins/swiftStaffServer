package swiftstaff

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.routing
import org.apache.log4j.BasicConfigurator
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.litote.kmongo.eq


// Main server
fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(CORS) {
        anyHost()
        method(HttpMethod.Options)
        allowNonSimpleContentTypes = true
    }
    // JSON / GSON conversion
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
        }
    }
    routing {
        get("/api/v1/jobs") {
            val jobs = MongoDatabase.find<Job>()
            if(jobs.isNotEmpty()){
                call.respond(status = HttpStatusCode.OK,  message = Jobs(jobs.size, jobs))
            }
        }


        post("/api/v1/jobs"){
            val job = call.receive<Job>()
            val success  = MongoDatabase.insert(job)
            if(success){
                call.respond(status = HttpStatusCode.Created, message = mapOf("id" to job._id))
            } else{
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    embeddedServer(Netty, 8080, module = Application::module).start()
}



