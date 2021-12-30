package ru.krea.database

import org.jetbrains.exposed.sql.Table

object Marks: Table() {
    val mId = integer("id").autoIncrement()
    val monthName = varchar("monthName", 50)
    val userLogin = varchar("userLogin", 50)
    val q1 = integer("1_1")
    val q2 = integer("1_2")
    val q3 = integer("2_1")
    val q4 = integer("2_2")
    val q5 = integer("2_3")
    val q6 = integer("3_1")
    val q7 = integer("3_2")
    val q8 = integer("3_3")
    val q9 = integer("4_1")
    val q10 = integer("4_2")
    val q11 = integer("5_1")
    val q12 = integer("5_2")
    val q13 = integer("5_3")

    override val primaryKey = PrimaryKey(mId)
}