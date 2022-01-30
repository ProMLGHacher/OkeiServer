package ru.krea.database

import org.jetbrains.exposed.sql.Table
import ru.krea.database.Marks.autoIncrement
import ru.krea.database.ReportMonthData.default

object ReportTeachers: Table() {
    private val id = integer("id").autoIncrement()

    val monthName = varchar("monthName", 50)
    val userName = varchar("userName", 50)
    val countPoints = varchar("countPoints", 50).default("")
    val premium = varchar("premium", 50).default("")


    override val primaryKey = PrimaryKey(id)
}