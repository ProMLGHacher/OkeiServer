package ru.krea.routes.users

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.krea.database.Marks
import ru.krea.database.User
import ru.krea.database.UserLastChange
import ru.krea.models.user.Teacher

fun Route.usersRoute() {
    route("/months") {
        get("/{monthName}") {
            var respondList = mutableListOf<Teacher>()
            val monthName = call.parameters["monthName"].toString()

            transaction {

                var topTeacher = ""

                var previousUserMarkSumm = 0
                User.selectAll().forEach { userIT ->
                    var currentUserMarkSumm = 0
                    if (userIT[User.statusId] == 4) {
                        if (userIT[User.login] != topTeacher) {
                            Marks.select { Marks.monthName.eq(monthName) and Marks.userLogin.eq(userIT[User.login]) }.forEach {
                                currentUserMarkSumm += it[Marks.mark]
                            }
                            if (currentUserMarkSumm > previousUserMarkSumm) {
                                topTeacher = userIT[User.login]
                                previousUserMarkSumm = currentUserMarkSumm
                            }
                        }
                    }
                }

                User.selectAll().forEach { userIT ->
                    if (userIT[User.statusId] == 4) {
                        val teacher = Teacher()

                        teacher.name = userIT[User.name]
                        teacher.login = userIT[User.login]
                        if (userIT[User.login] == topTeacher) {
                            teacher.isKing = true
                        }
                        Marks.select { Marks.monthName.eq(monthName) and Marks.userLogin.eq(userIT[User.login]) }.forEach {
                            teacher.countPoint += it[Marks.mark]
                        }
                        UserLastChange.select { UserLastChange.monthName.eq(monthName) and UserLastChange.userLogin.eq(userIT[User.login]) }.forEach {
                            teacher.lastChange = it[UserLastChange.lastChange]
                        }
                        respondList += teacher
                    }
                }
            }
            call.respond(respondList)
        }



    }
}