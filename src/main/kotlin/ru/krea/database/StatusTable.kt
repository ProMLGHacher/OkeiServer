package ru.krea.database

import org.jetbrains.exposed.sql.Table

object Status: Table() {
    val statusId = varchar("statusId", 10)
    val status = varchar("status", 50)

    override val primaryKey = PrimaryKey(statusId)
}