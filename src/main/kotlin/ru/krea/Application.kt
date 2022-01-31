package ru.krea

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.krea.database.*
import ru.krea.global.startRefreshTimer
import ru.krea.models.user.RespondUser
import ru.krea.models.user.UserAuthData
import ru.krea.plugins.configureRouting


fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

//    csvToXLSX()

    install(ContentNegotiation) {
        gson()
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
        }
    }

    Database.connect(
        "jdbc:mysql://localhost:3306/", driver = "com.mysql.cj.jdbc.Driver",
        user = "root", password = "cf6cf6cf6", databaseConfig =
    DatabaseConfig.invoke{

        this.sqlLogger = CompositeSqlLogger()
        this.defaultSchema = Schema("dtbase")

    })

    startRefreshTimer()

    transaction {

        SchemaUtils.create(Criterion)
        SchemaUtils.create(User)
        SchemaUtils.create(Status)
        SchemaUtils.create(Month)
        SchemaUtils.create(Marks)
        SchemaUtils.create(UserLastChange)
        SchemaUtils.create(LogsTable)
        SchemaUtils.create(ReportMonthData)
        SchemaUtils.create(ReportTeachers)

        Month.selectAll().forEach { monthIT ->
            ReportMonthData.insert { repIT ->
                repIT[monthName] = monthIT[Month.monthName]
            }
        }

        Month.selectAll().forEach { monthIT ->
            User.selectAll().forEach { userIT ->
                if (userIT[User.statusId] == 4) {
                    ReportTeachers.insert {
                        it[monthName] = monthIT[Month.monthName]
                        it[userName] = userIT[User.name]
                    }
                }
            }
        }

        // заполнение таблицы со значениями оценок
//        User.selectAll().forEach { userIT ->
//            if (userIT[User.statusId] !in listOf(1, 2, 3)) {
//                Month.selectAll().forEach { monthIT ->
//                    Criterion.selectAll().forEach { criterionIT ->
//                        Marks.insert { markIT ->
//                            markIT[markId] = criterionIT[Criterion.criterionId]
//                            markIT[monthName] = monthIT[Month.monthName]
//                            markIT[userLogin] = userIT[User.login]
//                            markIT[mark] = 0
//                            markIT[lastAppraiser] = "пока никто не оценил"
//                            markIT[lastChange] = "нет изменений"
//                        }
//                    }
//                }
//            }
//        }
//
//
//        User.selectAll().forEach { userIT ->
//            if (userIT[User.statusId] == 4) {
//                Month.selectAll().forEach { monthIT ->
//                    UserLastChange.insert { ulsIT ->
//                        ulsIT[userLogin] = userIT[User.login]
//                        ulsIT[lastChange] = "нет изменений"
//                        ulsIT[monthName] = monthIT[Month.monthName]
//                    }
//                }
//            }
//        }
    }

    val secret = environment.config.property("ktor.jwt.secret").getString()
    val issuer = environment.config.property("ktor.jwt.issuer").getString()
    val audience = environment.config.property("ktor.jwt.audience").getString()
    val myRealm = environment.config.property("ktor.jwt.realm").getString()
    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT
                .require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build())

            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    routing {
        post("/login") {
            val user = call.receive<UserAuthData>()

            var res = false
            var respondUser = RespondUser()

            transaction {
                User.select { User.login eq user.login }.forEach {
                    if (it[User.password] == user.password) {
                        res = true
                        respondUser.status = it[User.statusId].toString()
                        respondUser.login = it[User.login]
                        respondUser.name = it[User.name]
                    }
                }
            }

            if (res) {
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
//                    .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                    .withClaim("username", user.login)
                    .sign(Algorithm.HMAC256(secret))

                respondUser.token = "Bearer $token"

                transaction {
                    Status.select { Status.statusId eq respondUser.status }.forEach {
                        respondUser.status = it[Status.status]
                    }
                }
                call.respond(respondUser)
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }

    configureRouting()

}

