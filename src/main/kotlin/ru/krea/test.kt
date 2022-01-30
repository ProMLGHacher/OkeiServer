package ru.krea

import excelkt.workbook
import excelkt.write
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.krea.database.LogsTable
import ru.krea.models.user.PremiumTeacher
import java.util.*

fun main() {
    var listPremiumTeacher: List<PremiumTeacher> = listOf(
        PremiumTeacher("Никола", "54", "15000"),
        PremiumTeacher("Олег", "600", "130000"), PremiumTeacher("Мерлан", "0", "0")
    )

    Database.connect(
        "jdbc:mysql://localhost:3306/", driver = "com.mysql.cj.jdbc.Driver",
        user = "root", password = "cf6cf6cf6", databaseConfig =
        DatabaseConfig.invoke{

            this.sqlLogger = CompositeSqlLogger()
            this.defaultSchema = Schema("dtbase")

        })

    transaction {
        LogsTable.deleteAll()
    }
}