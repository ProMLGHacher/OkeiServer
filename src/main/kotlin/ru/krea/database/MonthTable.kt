package ru.krea.database

import org.jetbrains.exposed.sql.Table

object Month: Table() {
    val monthName = varchar("monthName", 50)
    val lastChange = varchar("lastChange", 50)

    override val primaryKey = PrimaryKey(monthName)
}