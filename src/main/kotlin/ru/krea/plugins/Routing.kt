package ru.krea.plugins

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import ru.krea.global.PREMIUM_REPORTS_PATH
import ru.krea.routes.admin_panel.reportRoute
import ru.krea.routes.months.monthsRoute
import ru.krea.routes.users.usersRoute

fun Application.configureRouting() {
    routing {
        authenticate("auth-jwt") {
            monthsRoute()
            usersRoute()
            reportRoute()
        }
    }

    // Starting point for a Ktor app:
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    //фотки будут тут браться
    routing {
        route("/images") {
            static("/") {
                files("C:\\Users\\SU\\Desktop\\serverImages\\teacherImages\\")
            }
        }
        route("/reports") {
            static("/") {
                files(PREMIUM_REPORTS_PATH)
            }
        }
    }
}
