package ru.krea.database

import org.jetbrains.exposed.sql.Table
import ru.krea.database.Marks.autoIncrement

object Logs: Table() {
    val lId = integer("id").autoIncrement()
    val apprName = varchar("apprName", 50)
    val date = varchar("date", 50)
    val userName = varchar("userName", 50)
    val l1 = integer("1_1")
    val l2 = integer("1_2")
    val l3 = integer("2_1")
    val l4 = integer("2_2")
    val l5 = integer("2_3")
    val l6 = integer("3_1")
    val l7 = integer("3_2")
    val l8 = integer("3_3")
    val l9 = integer("4_1")
    val l10 = integer("4_2")
    val l11 = integer("5_1")
    val l12 = integer("5_2")
    val l13 = integer("5_3")

    override val primaryKey = PrimaryKey(lId)
}