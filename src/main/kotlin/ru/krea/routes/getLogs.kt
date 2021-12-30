package ru.krea.routes

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.krea.database.Logs
import java.io.File

fun Route.getLogs() {
    route("/getLogs") {

        static("st") {
            files("C:\\Users\\SU\\Desktop\\")
        }

        get {

            var rows = mutableListOf(mutableListOf("кто оценил", "кого оценили", "дата оценивания", "1.1", "1.2", "2.1", "2.2", "2.3", "3.1", "3.2", "3.3", "4.1", "4.2", "5.1", "5.2", "5.3"))

            transaction {

                SchemaUtils.setSchema(Schema("dtbase"))

                Logs.selectAll().forEach {

                    var list = mutableListOf<String>()

                    list.add(it[Logs.apprName])
                    list.add(it[Logs.userName])
                    list.add(it[Logs.date])
                    list.add(it[Logs.l1].toString())
                    list.add(it[Logs.l2].toString())
                    list.add(it[Logs.l3].toString())
                    list.add(it[Logs.l4].toString())
                    list.add(it[Logs.l5].toString())
                    list.add(it[Logs.l6].toString())
                    list.add(it[Logs.l7].toString())
                    list.add(it[Logs.l8].toString())
                    list.add(it[Logs.l9].toString())
                    list.add(it[Logs.l10].toString())
                    list.add(it[Logs.l11].toString())
                    list.add(it[Logs.l12].toString())
                    list.add(it[Logs.l13].toString())

                    rows.add(list)

                }
            }

            csvWriter().writeAll(rows, "C:\\Users\\SU\\Desktop\\logs.csv")

            val file = File("C:\\Users\\SU\\Desktop\\logs.csv")
            if(file.exists()) {
                call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "logs.csv")
                    .toString()
                )
                call.respondFile(file)
            }

        }


    }
}