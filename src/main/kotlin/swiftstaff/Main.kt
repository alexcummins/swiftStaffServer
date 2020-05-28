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
import io.ktor.response.respondText
import io.ktor.routing.routing
import org.apache.log4j.BasicConfigurator
import io.ktor.routing.*




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
//            call.respondText(DatabaseAdaptor.test(), ContentType.Text.Html)
        }
    }
}

fun main(args: Array<String>) {
    BasicConfigurator.configure()
//    print(DatabaseAdaptor.test())
//    embeddedServer(Netty, 8080, module = Application::module).start()
}



