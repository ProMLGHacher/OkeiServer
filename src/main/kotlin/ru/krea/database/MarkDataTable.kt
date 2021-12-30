package ru.krea.database

import org.jetbrains.exposed.sql.Table

object MarksData: Table() {
    val id = integer("id").autoIncrement()
    val month = varchar("month", 50)
    val login = varchar("login", 50)
    val markId = varchar("markId", 50)
    val lastLogin = varchar("lastLogin", 50)
    val lastDate = varchar("lastDate", 50)


    override val primaryKey = PrimaryKey(id)
}