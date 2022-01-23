package ru.krea.database

import org.jetbrains.exposed.sql.Table

object UserLastChange: Table() {

    private val id = integer("id").autoIncrement()
    val monthName = varchar("monthName", 50)
    val userLogin = varchar("userLogin", 50)
    val lastChange = varchar("lastChange", 50)

    override val primaryKey = PrimaryKey(id)
}