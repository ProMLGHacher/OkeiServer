package ru.krea.routes.months

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.krea.database.Marks
import ru.krea.database.Month
import ru.krea.database.User
import ru.krea.global.MAX_MARK_VALUE_FOR_TEACHER
import ru.krea.global.MONTHS_NAMES
import ru.krea.models.month.MonthData
import java.util.*
import kotlin.math.floor

fun Route.monthsRoute() {
    route("months") {
        get {

            var previousMonth = MonthData()
            var currentMonth = MonthData()

            var previousMonthTeacherCount = 0
            var currentMonthTeacherCount = 0

            val now = Calendar.getInstance()!!
            var monthId = now.get(Calendar.MONTH)

            transaction {
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
                    currentMonthTeacherCount ++
                }
                currentMonth.progress /= currentMonthTeacherCount * MAX_MARK_VALUE_FOR_TEACHER
                currentMonth.progress *= 100
                currentMonth.progress = floor(currentMonth.progress.toDouble()).toFloat()

                Marks.select { Marks.monthName eq previousMonth.name }.forEach {
                    previousMonth.progress += it[Marks.mark]
                    previousMonthTeacherCount ++
                }
                previousMonth.progress /= previousMonthTeacherCount * MAX_MARK_VALUE_FOR_TEACHER
                previousMonth.progress *= 100
                previousMonth.progress = floor(previousMonth.progress.toDouble()).toFloat()


                for (i in 0..4) {
                    var currentUserMarkSumm = 0
                    var previousUserMarkSumm = 0
                    User.selectAll().forEach { userIT ->
                        if (userIT[User.statusId] == 4) {
                            if (userIT[User.login] !in currentMonth.topTeachers) {
                                Marks.select { Marks.monthName.eq(currentMonth.name) and Marks.userLogin.eq(userIT[User.login]) }.forEach {
                                    currentUserMarkSumm += it[Marks.mark]
                                }
                                if (currentUserMarkSumm > previousUserMarkSumm) {
                                    currentMonth.topTeachers[i] = userIT[User.login]
                                }
                                previousUserMarkSumm = currentUserMarkSumm
                            }
                        }
                    }
                }

                for (i in 0..4) {
                    var currentUserMarkSumm = 0
                    var previousUserMarkSumm = 0
                    User.selectAll().forEach { userIT ->
                        if (userIT[User.statusId] == 4) {
                            if (userIT[User.login] !in previousMonth.topTeachers) {
                                Marks.select { Marks.monthName.eq(previousMonth.name) and Marks.userLogin.eq(userIT[User.login]) }.forEach {
                                    currentUserMarkSumm += it[Marks.mark]
                                }
                                if (currentUserMarkSumm > previousUserMarkSumm) {
                                    previousMonth.topTeachers[i] = userIT[User.login]
                                }
                                previousUserMarkSumm = currentUserMarkSumm
                            }
                        }
                    }
                }
            }
            call.respond(listOf(currentMonth, previousMonth))
        }
    }
}