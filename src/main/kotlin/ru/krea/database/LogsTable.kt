package ru.krea.database

import org.jetbrains.exposed.sql.Table

object LogsTable: Table() {
    private val id = integer("id").autoIncrement()
    val markId = varchar("номер оценки", 5)
    val appriseDate = varchar("дата оценивания", 50)
    val teacherLogin = varchar("учитель", 50)
    val appraiserLogin = varchar("оценивающий", 50)
    val mark = integer("оценка")

    override val primaryKey = PrimaryKey(id)
}