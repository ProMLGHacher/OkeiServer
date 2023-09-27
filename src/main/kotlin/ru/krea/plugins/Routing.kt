package ru.krea.plugins

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.krea.database.*
import ru.krea.global.PREMIUM_REPORTS_PATH
import ru.krea.global.PasswordManager
import ru.krea.global.passwordManager
import ru.krea.global.stringTolatyn
import ru.krea.models.criterion.VoteCriterion
import ru.krea.models.user.UserAuthData
import ru.krea.models.user.UserData
import ru.krea.routes.admin_panel.reportRoute
import ru.krea.routes.months.monthsRoute
import ru.krea.routes.users.usersRoute
import java.io.File
import kotlin.random.Random

fun Application.configureRouting() {
    routing {
        authenticate("auth-jwt") {
            usersRoute()
            reportRoute()
        }
        monthsRoute()
    }

    // Starting point for a Ktor app:
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    //фотки будут тут драться
    routing {
        route("/images") {
            static("/") {
                files("C:\\Users\\SU\\Desktop\\serverImages\\teacherImages\\")
            }
        }
        route("/reports") {
            get {
                call.respond(File(PREMIUM_REPORTS_PATH).listFiles())
            }
            get("/{dir}") {
                val dir = call.parameters["dir"].toString()
                call.respond(File(PREMIUM_REPORTS_PATH + dir + "\\").listFiles())
            }
            static("/stat/") {
                files(PREMIUM_REPORTS_PATH)
            }
        }
    }

    routing {
        route("deleteUser") {
            delete("/{login}") {
                val userLogin = call.parameters["login"].toString()

                transaction {
                    Marks.deleteWhere { Marks.userLogin eq userLogin }
                    UserLastChange.deleteWhere { UserLastChange.userLogin eq userLogin }
                    User.deleteWhere { User.login eq userLogin }
                    User.select {
                        User.login.eq(userLogin)
                    }.forEach { user ->
                        ReportTeachers.deleteWhere {
                            ReportTeachers.userName.eq(user[User.name])
                        }
                    }
                }

                call.respond("ok")

            }
        }

        route("addUser") {
            put {
                val user = call.receive<UserData>()
                val userLogin = stringTolatyn(user.name.split(" ")[0].toLowerCase()) + Random.nextInt(10, 99).toString()
                val password = passwordManager.generatePassword(
                    isWithLetters = true,
                    isWithUppercase = true,
                    isWithNumbers = true,
                    isWithSpecial = false,
                    length = 5
                )
                val name = user.name
                val statusID = getStatusIDFromStatusName(user.status)

                transaction {
                    User.insert {
                        it[login] = userLogin
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

                call.respond(HttpStatusCode.OK)

            }
        }

        route("updateNameUser") {
            patch {
                val user = call.receive<UserData>()
                transaction {
                    User.select { User.login.eq(user.login) }.forEach { userIT ->
                        ReportTeachers.update({ReportTeachers.userName.eq(userIT[User.name])}) {
                            it[ReportTeachers.userName] = user.name
                        }
                        User.update({User.login.eq(user.login)}) {
                            it[User.name] = user.name
                            it[User.statusId] = getStatusIDFromStatusName(user.status)
                        }
                    }
                }
                call.respond(HttpStatusCode.OK)
            }
        }

        route("getAllUsers") {
            get {
                val respondList = mutableListOf<UserData>()
                transaction {
                    User.selectAll().forEach {
                        respondList.add(
                            UserData(
                                name = it[User.name],
                                login = it[User.login],
                                status = getStatusNameFromStatusID(it[User.statusId])
                            )
                        )
                    }
                }
                call.respond(respondList)
            }
        }

    }

}

fun getStatusNameFromStatusID(id: Int): String = when(id) {
    1 -> "Директор"
    2 -> "Админ"
    3 -> "Оценивающий"
    4 -> "Учитель"
    else -> "Позовите админа))"
}

fun getStatusIDFromStatusName(name: String): Int = when(name) {
    "Директор" -> 1
    "Админ" -> 2
    "Оценивающий" -> 3
    "Учитель" -> 4
    else -> 999999
}