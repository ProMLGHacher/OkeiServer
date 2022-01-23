package ru.krea.global

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*

val now = Calendar.getInstance()!!
var yr = if(now.get(Calendar.MONTH) < 8) {
    Year.now().value
} else {
    Year.now().value + 1
}


fun getRefreshDate(): String {
    return "$yr-08-01 00:00:00"
}

fun startRefreshTimer() {
    val dateFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date: Date = dateFormatter.parse(getRefreshDate())
    val timer = Timer()
    timer.schedule(MyTimeTask(), date)
}

class MyTimeTask : TimerTask() {
    override fun run() {

        println(yr)



        yr += 1
        startRefreshTimer()
    }
}