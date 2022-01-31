package ru.krea.database

import org.jetbrains.exposed.sql.Table

object ReportMonthData: Table() {
    val monthName = varchar("monthName", 50)
    val urlAccount = varchar("urlAccount", 400).default("")
    val totalAmountPremium = varchar("totalAmountPremium", 50).default("")
    val fixedPremium = varchar("fixedPremium", 50).default("")
    val totalAmountPoints = varchar("totalAmountPoints", 50).default("")
    val partSemiannualPremium = varchar("partSemiannualPremium", 50).default("")
    val distributablePremium = varchar("distributablePremium", 50).default("")
    val pointValue = varchar("pointValue", 50).default("")

    override val primaryKey = PrimaryKey(monthName)
}