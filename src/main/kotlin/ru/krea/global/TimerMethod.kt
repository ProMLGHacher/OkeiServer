package ru.krea.global

import excelkt.workbook
import excelkt.write
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.krea.database.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*

fun getYrRefreshDate(): String {
    var now = Calendar.getInstance()!!
    var yr = if(now.get(Calendar.MONTH) < 6) {
        Year.now().value
    } else {
        Year.now().value + 1
    }
    return "$yr-07-01 00:00:00"
}

fun getMonthRefreshDate(): String {
    var cal = Calendar.getInstance()!!
    var yr = Year.now().value
    var mon = cal.get(Calendar.MONTH) + 2

    return "$yr-$mon-01 00:00:00"
}

fun startRefreshTimer() {
    startMonthTimer()
    startYrTimer()
}

fun startMonthTimer() {
    val dateFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val monthDate: Date = dateFormatter.parse(getMonthRefreshDate())
    val monthTimer = Timer()
    monthTimer.schedule(MonthTimer(), monthDate)
}

fun startYrTimer() {
    val dateFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val yrDate: Date = dateFormatter.parse(getYrRefreshDate())
    val yrTimer = Timer()
    yrTimer.schedule(YearTimer(), yrDate)
}

class YearTimer : TimerTask() {
    override fun run() {

        //надо ещё сделать сохранение отчёта

        transaction {
            Marks.update {
                it[mark] = 0
                it[lastChange] = "нет изменений"
                it[lastAppraiser] = "пока никто не оценил"
            }
            Month.update {
                it[lastChange] = "нет"
            }
            User.update {
                it[lastChange] = "нет изменений"
            }
            UserLastChange.update {
                it[lastChange] = "нет изменений"
            }
            ReportMonthData.deleteAll()
            Month.selectAll().forEach { monthIT ->
                ReportMonthData.insert { repIT ->
                    repIT[monthName] = monthIT[Month.monthName]
                }
            }
            ReportTeachers.deleteAll()
            Month.selectAll().forEach { monthIT ->
                User.selectAll().forEach { userIT ->
                    if (userIT[User.statusId] == 4) {
                        ReportTeachers.insert {
                            it[monthName] = monthIT[Month.monthName]
                            it[userName] = userIT[User.name]
                        }
                    }
                }
            }
        }

        startYrTimer()
    }
}

class MonthTimer : TimerTask() {
    override fun run() {

        val now = Calendar.getInstance()!!
        val year = now.get(Calendar.YEAR)
        val month = MONTHS_NAMES[now.get(Calendar.MONTH)]

        //сохранение отчётности
        workbook {
            sheet {
                row {
                    cell("Номер оценки")
                    cell("Дата оценивания")
                    cell("Учитель")
                    cell("Оценивающий")
                    cell("Оценка")
                }
                transaction {
                    LogsTable.selectAll().forEach {
                        row {
                            cell(it[LogsTable.markId])
                            cell(it[LogsTable.appriseDate])
                            cell(it[LogsTable.teacherLogin])
                            cell(it[LogsTable.appraiserLogin])
                            cell(it[LogsTable.mark])
                        }
                    }
                }
            }
        }.write("$PREMIUM_LOGS_PATH$year $month.xlsx")

        transaction {
            LogsTable.deleteAll()
        }

        startMonthTimer()
    }
}

class TestTimer : TimerTask() {
    override fun run() {
        println("ку")
    }
}