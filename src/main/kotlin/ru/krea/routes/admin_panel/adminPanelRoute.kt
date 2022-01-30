package ru.krea.routes.admin_panel

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import ru.krea.models.Report
import ru.krea.models.ValuesCalculatingMonth

fun Route.reportRoute() {
    route("report") {
        get("/{month}") {

            val monthName = call.parameters["month"].toString()

            if (monthName == "Январь") {
                call.response.status(HttpStatusCode(555, "NeedCreateResource"))
                call.respond(ValuesCalculatingMonth())
            } else {
                call.respond(Report())
            }
        }
    }
}
