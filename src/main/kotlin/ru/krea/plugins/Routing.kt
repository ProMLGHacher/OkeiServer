package ru.krea.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.request.*
import ru.krea.routes.getLogs
import ru.krea.routes.monthsRouting

fun Application.configureRouting() {

    routing {

        authenticate("auth-jwt") {
            monthsRouting()
        }

        getLogs()

    }

    // Starting point for a Ktor app:
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    routing {
        route("/images") {
            static("/") {
                files("C:\\Users\\SU\\Desktop\\serverImages\\teacherImages\\")
            }
        }
    }
}
