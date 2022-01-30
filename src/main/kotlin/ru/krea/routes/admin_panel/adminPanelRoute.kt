package ru.krea.routes.admin_panel

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import ru.krea.models.Report

fun Route.reportRoute() {
    route("report") {
        get("/{month}") {

            val monthName = call.parameters["month"].toString()

            if (monthName == "Январь") {
                call.response.status(HttpStatusCode.NoContent)
                call.respond("3214")
            } else {
                call.respond(Report())
            }
        }
    }
}
