package ru.krea.routes.months

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.krea.database.*
import ru.krea.global.MAX_MARK_VALUE_FOR_TEACHER
import ru.krea.global.MONTHS_NAMES
import ru.krea.models.criterion.VoteCriterion
import ru.krea.models.month.MonthData
import ru.krea.models.user.UserAuthData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.floor

fun Route.monthsRoute() {
    route("months") {
        get {

            val previousMonth = MonthData()
            val currentMonth = MonthData()

            var teacherCount = 0

            val now = Calendar.getInstance()!!
            val monthId = now.get(Calendar.MONTH)

            transaction {

                User.select { User.statusId eq 4 }.forEach { _ ->
                    teacherCount += 1
                }

                Month.select { Month.monthName eq MONTHS_NAMES[monthId] }.forEach {
                    currentMonth.name = it[Month.monthName]
                    currentMonth.underway = true
                    currentMonth.lastChange = it[Month.lastChange]
                }
                Month.select { Month.monthName eq MONTHS_NAMES[if (monthId == 0) 11 else monthId-1] }.forEach {
                    previousMonth.name = it[Month.monthName]
                    previousMonth.underway = false
                    previousMonth.lastChange = it[Month.lastChange]
                }
                Marks.select { Marks.monthName eq currentMonth.name }.forEach {
                    currentMonth.progress += it[Marks.mark]
                }
                println(currentMonth.progress)
                currentMonth.progress /= teacherCount * MAX_MARK_VALUE_FOR_TEACHER
                currentMonth.progress *= 100
                currentMonth.progress = floor(currentMonth.progress.toDouble()).toFloat()
                println(currentMonth.progress)

                Marks.select { Marks.monthName eq previousMonth.name }.forEach {
                    previousMonth.progress += it[Marks.mark]
                }
                previousMonth.progress /= teacherCount * MAX_MARK_VALUE_FOR_TEACHER
                previousMonth.progress *= 100
                previousMonth.progress = floor(previousMonth.progress.toDouble()).toFloat()


                for (i in 0..4) {
                    var previousUserMarkSumm = 0
                    User.selectAll().forEach { userIT ->
                        if (userIT[User.statusId] == 4) {
                            if (userIT[User.name] !in currentMonth.topTeachers) {

                                var currentUserMarkSumm = 0

                                Marks.select { Marks.monthName.eq(currentMonth.name) and Marks.userLogin.eq(userIT[User.login]) }.forEach {
                                    currentUserMarkSumm += it[Marks.mark]
                                }
                                if (currentUserMarkSumm > previousUserMarkSumm) {
                                    currentMonth.topTeachers[i] = userIT[User.name]
                                    previousUserMarkSumm = currentUserMarkSumm
                                }
                            }
                        }
                    }
                }

                for (i in 0..4) {
                    var previousUserMarkSumm = 0
                    User.selectAll().forEach { userIT ->
                        if (userIT[User.statusId] == 4) {

                            var currentUserMarkSumm = 0

                            if (userIT[User.name] !in previousMonth.topTeachers) {
                                Marks.select { Marks.monthName.eq(previousMonth.name) and Marks.userLogin.eq(userIT[User.login]) }.forEach {
                                    currentUserMarkSumm += it[Marks.mark]
                                }
                                if (currentUserMarkSumm > previousUserMarkSumm) {
                                    previousMonth.topTeachers[i] = userIT[User.name]
                                    previousUserMarkSumm = currentUserMarkSumm
                                }
                            }
                        }
                    }
                }
            }
            call.respond(listOf(currentMonth, previousMonth))
        }

        get("/{monthName}/{loginTeacher}") {
            var monthName = call.parameters["monthName"].toString()
            val loginTeacher = call.parameters["loginTeacher"].toString()

            if (monthName == "this") {
                val now = Calendar.getInstance()!!
                val monthID = now.get(Calendar.MONTH)
                monthName = MONTHS_NAMES[monthID]
            }

            val respondList = mutableListOf<VoteCriterion>()

            transaction {
                Marks.select{ Marks.monthName.eq(monthName) and Marks.userLogin.eq(loginTeacher)}.forEach {
                    val voteCriterion = VoteCriterion(
                        it[Marks.markId],
                        it[Marks.lastAppraiser],
                        it[Marks.lastChange],
                        it[Marks.mark]
                    )
                    respondList += voteCriterion
                }
            }
            call.respond(respondList)
        }

        put("/{monthName}/{loginTeacher}") {
            val now = Calendar.getInstance()!!
            val day = now.get(Calendar.DAY_OF_MONTH)

            if (day > 25) {
                call.respondText("MethodNotAllowed", status = HttpStatusCode.MethodNotAllowed)
            } else {
                val monthName = call.parameters["monthName"].toString()
                val loginTeacher = call.parameters["loginTeacher"].toString()
                var teacherName = ""

                val voteCriterion = call.receive<VoteCriterion>()

                if (voteCriterion.lastChange == "") {
                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val formatted = current.format(formatter)

                    voteCriterion.lastChange = formatted
                }

                transaction {
                    User.select { User.login.eq(loginTeacher) }.forEach {
                        teacherName = it[User.name]
                    }
                    Marks.update ({ Marks.monthName.eq(monthName) and Marks.userLogin.eq(loginTeacher) and Marks.markId.eq(voteCriterion.id)}) {
                        it[mark] = voteCriterion.points
                        it[lastChange] = voteCriterion.lastChange
                        it[lastAppraiser] = voteCriterion.nameAppraiser
                    }
                    Month.update({Month.monthName.eq(monthName)}) {
                        it[lastChange] = voteCriterion.lastChange
                    }
                    UserLastChange.update({UserLastChange.monthName.eq(monthName) and UserLastChange.userLogin.eq(loginTeacher)}) {
                        it[lastChange] = voteCriterion.lastChange
                    }
                    LogsTable.insert {
                        it[appraiserLogin] = voteCriterion.nameAppraiser
                        it[teacherLogin] = teacherName
                        it[mark] = voteCriterion.points
                        it[markId] = voteCriterion.id
                        it[appriseDate] = voteCriterion.lastChange
                    }
                }
                call.respond(HttpStatusCode.Created)
            }
        }
    }
}