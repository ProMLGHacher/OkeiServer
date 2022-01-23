package ru.krea.database

import org.jetbrains.exposed.sql.Table

object User: Table() {
    val login = varchar("login", 50)
    val password = varchar("password", 50)
    val name = varchar("name", 50)
    val statusId = integer("statusId")
    val lastChange = varchar("lastChange", 50)

    override val primaryKey = PrimaryKey(login)
}