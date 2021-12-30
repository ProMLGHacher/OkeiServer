package ru.krea.database

import org.jetbrains.exposed.sql.Table

object User: Table() {
    val userId = integer("userId").autoIncrement()
    val login = varchar("login", 50)
    val password = varchar("password", 50)
    val name = varchar("name", 50)
    val statusId = integer("statusId").references(Status.statusId)

    override val primaryKey = PrimaryKey(userId)
}