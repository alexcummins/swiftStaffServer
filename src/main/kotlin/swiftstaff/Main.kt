package swiftstaff

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.routing.routing
import org.apache.log4j.BasicConfigurator
import io.ktor.routing.*
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
        get("/") {
//            call.respondText(MongoDatabase.find(), ContentType.Text.Html)
        }
    }
}
data class Alex(val name: String, val age: Int)

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    MongoDatabase.db
//    embeddedServer(Netty, 8080, module = Application::module).start()
}



