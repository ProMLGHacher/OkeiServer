package ru.krea.database

import org.jetbrains.exposed.sql.Table

object Status: Table() {
    val statusId = integer("statusId").autoIncrement()
    val status = varchar("status", 50)

    override val primaryKey = PrimaryKey(statusId)
}