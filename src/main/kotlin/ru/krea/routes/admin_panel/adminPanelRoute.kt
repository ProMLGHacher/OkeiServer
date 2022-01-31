package ru.krea.routes.admin_panel

import excelkt.workbook
import excelkt.write
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.krea.database.Marks
import ru.krea.database.ReportMonthData
import ru.krea.database.ReportTeachers
import ru.krea.database.User
import ru.krea.global.PREMIUM_REPORTS_LINK
import ru.krea.global.PREMIUM_REPORTS_PATH
import ru.krea.models.CreateReport
import ru.krea.models.Report
import ru.krea.models.ValuesCalculatingMonth
import ru.krea.models.user.PremiumTeacher
import java.io.File
import java.util.*

fun Route.reportRoute() {
    route("report") {
        get("/{month}") {

            val principal = call.principal<JWTPrincipal>()
            val userlogin = principal!!.payload.getClaim("username").asString()
            var username = ""

            val monthName = call.parameters["month"].toString()

            // если месяц текущий
            if (monthName == monthName) {

                var statId: Int = 0

                transaction {
                    User.select { User.login.eq(userlogin) }.forEach {
                        statId = it[User.statusId]
                        username = it[User.name]
                    }
                }

                // если дирик
                if (statId == 1) {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond("")
                } else {

                    //если отчёт есть
                    if (File("$PREMIUM_REPORTS_PATH$monthName.xlsx").exists()) {

                        var report = Report()

                        var listPremiumTeacher = mutableListOf<PremiumTeacher>()

                        transaction {
                            ReportMonthData.select { ReportMonthData.monthName.eq(monthName) }.forEach {
                                report.totalAmountPremium = it[ReportMonthData.totalAmountPremium]
                                report.fixedPremium = it[ReportMonthData.fixedPremium]
                                report.totalAmountPoints = it[ReportMonthData.totalAmountPoints]
                                report.partSemiannualPremium = it[ReportMonthData.partSemiannualPremium]
                                report.distributablePremium = it[ReportMonthData.distributablePremium]
                                report.pointValue = it[ReportMonthData.pointValue]
                                report.urlExcel = it[ReportMonthData.urlAccount]
                            }
                            ReportTeachers.select { ReportTeachers.monthName.eq(monthName) and ReportTeachers.userName.eq(username) }.forEach {
                                listPremiumTeacher += PremiumTeacher(it[ReportTeachers.userName], it[ReportTeachers.countPoints], it[ReportTeachers.premium])
                            }
                        }

                        report.listPremiumTeacher = listPremiumTeacher.toList()

                        call.respond(report)

                    } else {
                        call.response.status(HttpStatusCode(555, "NeedCreateResource"))

                        var totalAmountPoints: String = ""
                        var countTeacher: Int = 0

                        transaction {
                            ReportMonthData.select { ReportMonthData.monthName.eq(monthName) }.forEach {
                                totalAmountPoints = it[ReportMonthData.totalAmountPoints]
                            }

                            User.select { User.statusId.eq(4) }.forEach {
                                countTeacher += 1
                            }
                        }
                        call.respond(ValuesCalculatingMonth(totalAmountPoints, countTeacher))
                    }
                }
            }
            else {
                //если отчёт есть
                if (File("$PREMIUM_REPORTS_PATH$monthName.xlsx").exists()) {

                    var report = Report()

                    var listPremiumTeacher = mutableListOf<PremiumTeacher>()

                    transaction {
                        ReportMonthData.select { ReportMonthData.monthName.eq(monthName) }.forEach {
                            report.totalAmountPremium = it[ReportMonthData.totalAmountPremium]
                            report.fixedPremium = it[ReportMonthData.fixedPremium]
                            report.totalAmountPoints = it[ReportMonthData.totalAmountPoints]
                            report.partSemiannualPremium = it[ReportMonthData.partSemiannualPremium]
                            report.distributablePremium = it[ReportMonthData.distributablePremium]
                            report.pointValue = it[ReportMonthData.pointValue]
                            report.urlExcel = it[ReportMonthData.urlAccount]
                        }
                        ReportTeachers.select { ReportTeachers.monthName.eq(monthName) and ReportTeachers.userName.eq(username) }.forEach {
                            listPremiumTeacher += PremiumTeacher(it[ReportTeachers.userName], it[ReportTeachers.countPoints], it[ReportTeachers.premium])
                        }
                    }

                    report.listPremiumTeacher = listPremiumTeacher.toList()

                    call.respond(report)

                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        post("/{month}") {
            val monthName = call.parameters["month"].toString()
            val createReportData = call.receive<CreateReport>()
            val report = Report()

            val now = Calendar.getInstance()!!
            val year = now.get(Calendar.YEAR)

            var folder = File("$PREMIUM_REPORTS_PATH$year")
            folder.mkdir()

            transaction {
                report.totalAmountPremium = createReportData.totalAmountPremium
                report.fixedPremium = createReportData.fixedPremium
                report.totalAmountPoints = createReportData.totalAmountPoints
                report.partSemiannualPremium = createReportData.partSemiannualPremium
                report.distributablePremium = createReportData.distributablePremium
                report.pointValue = createReportData.pointValue

                val listPremiumTeacher = mutableListOf<PremiumTeacher>()

                User.selectAll().forEach { userIT ->
                    if (userIT[User.statusId] == 4) {
                        var markSumm = 0
                        Marks.select { Marks.userLogin.eq(userIT[User.login]) and Marks.monthName.eq(monthName) }.forEach {
                            markSumm += it[Marks.mark]
                        }
                        listPremiumTeacher += PremiumTeacher(userIT[User.name], markSumm.toString(),
                            (markSumm * report.pointValue.toInt()).toString()
                        )
                    }
                }

                report.listPremiumTeacher = listPremiumTeacher.toList()

                report.listPremiumTeacher.forEach { teacher ->
                    ReportTeachers.update({ReportTeachers.monthName.eq(monthName) and ReportTeachers.userName.eq(teacher.name)}) {
                        it[countPoints] = teacher.countPoints
                        it[premium] = teacher.premium
                    }
                }

                workbook {
                    sheet {
                        report.listPremiumTeacher.forEach { teacher ->
                            row {
                                cell(teacher.name)
                                cell(teacher.countPoints)
                                cell(teacher.premium)
                            }
                        }
                    }
                }.write("$PREMIUM_REPORTS_PATH$year\\$monthName.xlsx")

                report.urlExcel = "$PREMIUM_REPORTS_LINK$year\\$monthName.xlsx"

                ReportMonthData.update({ReportMonthData.monthName.eq(monthName)}) {
                    it[ReportMonthData.urlAccount] = report.urlExcel
                    it[ReportMonthData.fixedPremium] = report.fixedPremium
                    it[ReportMonthData.distributablePremium] = report.distributablePremium
                    it[ReportMonthData.partSemiannualPremium] = report.partSemiannualPremium
                    it[ReportMonthData.pointValue] = report.pointValue
                    it[ReportMonthData.totalAmountPremium] = report.totalAmountPremium
                    it[ReportMonthData.totalAmountPoints] = report.totalAmountPoints
                }

            }
            call.respond(report)
        }
    }
}
