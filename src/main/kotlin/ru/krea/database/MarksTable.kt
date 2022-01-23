package ru.krea.database

import org.jetbrains.exposed.sql.Table

object Marks: Table() {
    private val id = integer("id").autoIncrement()
    val markId = varchar("markId", 5)
    val monthName = varchar("monthName", 50)
    val userLogin = varchar("userLogin", 50)
    val mark = integer("markValue")

    override val primaryKey = PrimaryKey(id)
}