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
import ru.krea.database.User.login
import ru.krea.global.*
import ru.krea.models.RespondUser
import ru.krea.models.UserAuthData
import ru.krea.plugins.configureRouting
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    startRefreshTimer()

    install(ContentNegotiation) {
        gson()
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
        }
    }

    Database.connect(
        "jdbc:mysql://localhost:3306/", driver = "com.mysql.cj.jdbc.Driver",
        user = "root", password = "cf6cf6cf6")



    transaction {
        // print sql to std-out
        addLogger(StdOutSqlLogger)
        SchemaUtils.setSchema(Schema("dtbase"))

        SchemaUtils.create(Status)
        SchemaUtils.create(User)
        SchemaUtils.create(Marks)
        SchemaUtils.create(Logs)
        SchemaUtils.create(MarksData)

//        User.insert {
//            it[login] = "kisliy"
//            it[password] = "kisliy_228"
//            it[name] = "Кисленко"
//            it[imageURL] = "юрл"
//            it[statusId] = 3
//        }

//        MONTHS_NAMES.forEach { itMonth ->
//            User.selectAll().forEach { itUser ->
//                if (itUser[User.statusId] == 3) {
//                    Marks.insert {
//                        it[monthName] = itMonth
//                        it[userLogin] = itUser[User.login]
//                        it[q1] = 0
//                        it[q2] = 0
//                        it[q3] = 0
//                        it[q4] = 0
//                        it[q5] = 0
//                        it[q6] = 0
//                        it[q7] = 0
//                        it[q8] = 0
//                        it[q9] = 0
//                        it[q10] = 0
//                        it[q11] = 0
//                        it[q12] = 0
//                        it[q13] = 0
//                    }
//                }
//            }
//        }
//        val listooo = listOf("1.1", "1.2", "2.1", "2.2", "2.3", "3.1", "3.2", "3.3", "4.1", "4.2", "5.1", "5.2", "5.3")
//        MONTHS_NAMES.forEach { itMonth ->
//            User.selectAll().forEach { itUser ->
//                if (itUser[User.statusId] == 3) {
//                    listooo.forEach { listIT ->
//                        MarksData.insert {
//                            it[MarksData.month] = itMonth
//                            it[MarksData.login] = itUser[User.login]
//                            it[MarksData.markId] = listIT
//                            it[MarksData.lastDate] = "no"
//                            it[MarksData.lastLogin] = "no"
//                        }
//                    }
//                }
//            }
//        }


        User.selectAll().reversed().forEach { println(it[login]) }

//        User.selectAll().forEach {
//            val d = it[User.statusId]
//            Status.select { Status.statusId eq d }.forEach {
//                println(it[Status.status])
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


                addLogger(StdOutSqlLogger)
                SchemaUtils.setSchema(Schema("dtbase"))


                User.selectAll().forEach {
                    if (it[login] == user.login) {
                        println("1")
                        if (it[User.password] == user.password) {
                            println("2")
                            res = true
                        }
                    }
                }
            }

            if (res) {
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("username", user.login)
                    .sign(Algorithm.HMAC256(secret))

                respondUser.token = "Bearer $token"

                transaction {

                    addLogger(StdOutSqlLogger)
                    SchemaUtils.setSchema(Schema("dtbase"))

                    User.selectAll().forEach {
                        if (it[login] == user.login) {
                            respondUser.login = it[login]
                            respondUser.name = it[User.name]
                            respondUser.imageURL = IMAGES_PATH + it[login] + ".jpg"
                            respondUser.status = it[User.statusId].toString()
                        }
                    }

                    Status.select { Status.statusId eq respondUser.status.toInt() }.forEach {
                        respondUser.status = it[Status.status]
                    }

                }

                println(respondUser.toString())

                call.respond(respondUser)
            } else {

                println("LOH")

                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }

    configureRouting()

}

