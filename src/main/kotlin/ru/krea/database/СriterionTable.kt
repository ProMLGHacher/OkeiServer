package ru.krea.database

import org.jetbrains.exposed.sql.Table

object Criterion: Table() {
    val criterionId = varchar("criterionId", 10)
    val criterionName = varchar("criterionName", 1500)

    override val primaryKey = PrimaryKey(criterionId)
}