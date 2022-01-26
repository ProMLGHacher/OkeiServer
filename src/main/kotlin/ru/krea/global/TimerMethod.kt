package ru.krea.global

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
    monthTimer.schedule(YearTimer(), monthDate)
}

fun startYrTimer() {
    val dateFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val yrDate: Date = dateFormatter.parse(getYrRefreshDate())
    val yrTimer = Timer()
    yrTimer.schedule(MonthTimer(), yrDate)
}

class YearTimer : TimerTask() {
    override fun run() {

        //надо ещё сделать сохранение отчёта

        transaction {
            Marks.update {
                it[mark] = 0
                it[lastChange] = "нет изменений"
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
        }

        startYrTimer()
    }
}

class MonthTimer : TimerTask() {
    override fun run() {

        //сохранение отчётности

        startMonthTimer()
    }
}

class TestTimer : TimerTask() {
    override fun run() {
        println("ку")
    }
}