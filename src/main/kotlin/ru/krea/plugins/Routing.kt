package ru.krea.plugins

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.krea.database.*
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

    routing {
        route("delete") {
            get("/{login}") {
                val userLogin = call.parameters["login"].toString()

                transaction {
                    Marks.deleteWhere { Marks.userLogin eq userLogin }
                    UserLastChange.deleteWhere { UserLastChange.userLogin eq userLogin }
                    User.deleteWhere { User.login eq userLogin }
                }

            }
        }

        route("add") {
            get("/{login}/{password}/{name}/{statusID}") {
                val userLogin = call.parameters["login"].toString()
                val password = call.parameters["password"].toString()
                val name = call.parameters["name"].toString()
                val statusID = call.parameters["statusID"]!!.toInt()

                transaction {
                    User.insert {
                        it[login] = login
                        it[User.name] = name
                        it[statusId] = statusID
                        it[User.password] = password
                        it[lastChange] = "нет изменений"
                    }
                    Criterion.selectAll().forEach { criterionIT ->
                        Month.selectAll().forEach { monthIT ->
                            Marks.insert {
                                it[mark] = 0
                                it[Marks.userLogin] = userLogin
                                it[markId] = criterionIT[Criterion.criterionId]
                                it[monthName] = monthIT[Month.monthName]
                                it[lastChange] = "нет изменений"
                                it[lastAppraiser] = "пока никто не оценил"
                            }
                        }
                    }

                    Month.selectAll().forEach { monthIT ->
                        UserLastChange.insert {
                            it[lastChange] = "нет изменений"
                            it[UserLastChange.userLogin] = userLogin
                            it[monthName] = monthIT[Month.monthName]
                        }
                    }

                }

            }
        }
    }

}
