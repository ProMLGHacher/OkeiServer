//package ru.krea.routes
//
//import io.ktor.application.*
//import io.ktor.auth.*
//import io.ktor.auth.jwt.*
//import io.ktor.request.*
//import io.ktor.response.*
//import io.ktor.routing.*
//import org.jetbrains.exposed.sql.*
//import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
//import org.jetbrains.exposed.sql.transactions.transaction
//import ru.krea.database.*
//import ru.krea.database.Marks.q1
//import ru.krea.database.Marks.q10
//import ru.krea.database.Marks.q11
//import ru.krea.database.Marks.q12
//import ru.krea.database.Marks.q13
//import ru.krea.database.Marks.q2
//import ru.krea.database.Marks.q3
//import ru.krea.database.Marks.q4
//import ru.krea.database.Marks.q5
//import ru.krea.database.Marks.q6
//import ru.krea.database.Marks.q7
//import ru.krea.database.Marks.q8
//import ru.krea.database.Marks.q9
//import ru.krea.database.Marks.userLogin
//import ru.krea.global.MONTHS_NAMES
//import ru.krea.global.getListOfLogin
//import ru.krea.models.*
//import java.util.*
//
//fun Route.monthsRouting() {
//    route("/months") {
//        get {
//            val list = mutableListOf<MonthData>()
//            for (monthID in 0..11) {
//                var monthData = MonthData()
//                var leadCount = 0
//                var leadLogin = ""
//                transaction {
//
//                    addLogger(StdOutSqlLogger)
//                    SchemaUtils.setSchema(Schema("dtbase"))
//
//                    Marks.selectAll().forEach {
//                        if (it[Marks.monthName] == MONTHS_NAMES[monthID]) {
//
//                            var summ = 0
//
//                            summ += it[q1]
//                            summ += it[q2]
//                            summ += it[q3]
//                            summ += it[q4]
//                            summ += it[q5]
//                            summ += it[q6]
//                            summ += it[q7]
//                            summ += it[q8]
//                            summ += it[q9]
//                            summ += it[q10]
//                            summ += it[q11]
//                            summ += it[q12]
//                            summ += it[q13]
//
//                            if (summ > leadCount) {
//                                leadCount = summ
//                                leadLogin = it[userLogin]
//                            }
//
//                            monthData.progress += summ
//
//                        }
//                    }
//
//                }
//
//                transaction {
//                    addLogger(StdOutSqlLogger)
//                    SchemaUtils.setSchema(Schema("dtbase"))
//
//                    User.selectAll().forEach {
//                        if (it[User.login] == leadLogin) {
//                            monthData.leader = it[User.name]
//                        }
//                    }
//                }
//
//                monthData.nameMonth = MONTHS_NAMES[monthID]
//                monthData.progress /= 1980
//
//                val now = Calendar.getInstance()
//                println(now.time)
//                monthData.underway = if (now.get(Calendar.MONTH) == monthID) {
//                    println(now.get(Calendar.MONTH))
//                    println(monthID)
//                    true
//                } else {
//                    println(now.get(Calendar.MONTH))
//                    println(monthID)
//                    false
//                }
//
//
//                list += monthData
//
//            }
//
//            call.respond(list)
//        }
//
//
//        get("/{name}") {
//            var teacherDataList = mutableListOf<TeacherData>()
//            val monthName = call.parameters["name"]
//
//
//            transaction {
//                addLogger(StdOutSqlLogger)
//                SchemaUtils.setSchema(Schema("dtbase"))
//
//                Marks.selectAll().forEach {
//                    if (it[Marks.monthName] == monthName) {
//                        val teacherData = TeacherData()
//
//                        teacherData.login = it[userLogin]
//
//                        teacherData.countPoints += it[q1]
//                        teacherData.countPoints += it[q2]
//                        teacherData.countPoints += it[q3]
//                        teacherData.countPoints += it[q4]
//                        teacherData.countPoints += it[q5]
//                        teacherData.countPoints += it[q6]
//                        teacherData.countPoints += it[q7]
//                        teacherData.countPoints += it[q8]
//                        teacherData.countPoints += it[q9]
//                        teacherData.countPoints += it[q10]
//                        teacherData.countPoints += it[q11]
//                        teacherData.countPoints += it[q12]
//                        teacherData.countPoints += it[q13]
//
//                        User.selectAll().forEach { userIT ->
//                            if (userIT[User.login] == teacherData.login) {
//                                teacherData.name = userIT[User.name]
//                            }
//                        }
//
//                        teacherDataList += teacherData
//                        println(teacherData)
//
//                    }
//                }
//            }
//            println(teacherDataList)
//            call.respond(teacherDataList)
//        }
//
//        get("/{name}/{login}") {
//            val evaluationList = mutableListOf<Evaluation?>()
//
//            val principal = call.principal<JWTPrincipal>()
//            val username = principal!!.payload.getClaim("username").asString()
//
//            val monthName = call.parameters["name"]
//            val loginTeacher = call.parameters["login"]
//
//
//            transaction {
////                addLogger(StdOutSqlLogger)
//                SchemaUtils.setSchema(Schema("dtbase"))
//
//                Marks.selectAll().forEach {
//                    if (it[Marks.monthName] == monthName) {
//                        if (it[Marks.userLogin] == loginTeacher) {
//                            var evaluation = Evaluation()
//
//                            evaluation.numEval = q1.name.replace('_', '.')
//                            evaluation.countPoints = it[q1]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q2.name.replace('_', '.')
//                            evaluation.countPoints = it[q2]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q3.name.replace('_', '.')
//                            evaluation.countPoints = it[q3]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q4.name.replace('_', '.')
//                            evaluation.countPoints = it[q4]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q5.name.replace('_', '.')
//                            evaluation.countPoints = it[q5]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q6.name.replace('_', '.')
//                            evaluation.countPoints = it[q6]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q7.name.replace('_', '.')
//                            evaluation.countPoints = it[q7]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q8.name.replace('_', '.')
//                            evaluation.countPoints = it[q8]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q9.name.replace('_', '.')
//                            evaluation.countPoints = it[q9]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q10.name.replace('_', '.')
//                            evaluation.countPoints = it[q10]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q11.name.replace('_', '.')
//                            evaluation.countPoints = it[q11]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q12.name.replace('_', '.')
//                            evaluation.countPoints = it[q12]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q13.name.replace('_', '.')
//                            evaluation.countPoints = it[q13]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                        }
//                    }
//                }
//            }
//
//            println(evaluationList)
//            call.respond(evaluationList)
//        }
//
//        put("/{name}/{login}") {
////            var receiveDataLo: List<Evaluation?> = call.receive()
//            var receiveDataLoh: Eval = call.receive()
//            println(receiveDataLoh.list)
//            var receiveData = mutableListOf<Evaluation?>()
//            receiveData.addAll(receiveDataLoh.list)
//            receiveDataLoh.list.forEach {
//                println(it)
//            }
////            var lll = receiveData[0]
////            println(lll)
////            println("LOOOOOOOOOOOOH")
////            println(receiveData)
////
//            val evaluationList = mutableListOf<Evaluation?>()
////            println("ХУЙовина")
////
//            val principal = call.principal<JWTPrincipal>()
//            val username = principal!!.payload.getClaim("username").asString()
//
//            val monthName = call.parameters["name"]
//            val loginTeacher = call.parameters["login"]
//
//            var date = ""
//            var ques1 = 0
//            var ques2 = 0
//            var ques3 = 0
//            var ques4 = 0
//            var ques5 = 0
//            var ques6 = 0
//            var ques7 = 0
//            var ques8 = 0
//            var ques9 = 0
//            var ques10 = 0
//            var ques11 = 0
//            var ques12 = 0
//            var ques13 = 0
//
//
//            transaction {
////                addLogger(StdOutSqlLogger)
//                SchemaUtils.setSchema(Schema("dtbase"))
//
//
//                receiveData.forEach { rdIT ->
//                    if (rdIT != null) {
//                        Marks.selectAll().forEach {
//                            if (it[Marks.monthName] == monthName) {
//                                if (it[Marks.userLogin] == loginTeacher) {
//                                    if (q1.name.replace('_', '.') == rdIT.numEval) {
//                                        Marks.update({Marks.mId eq it[Marks.mId]}) { updateIT ->
//                                            updateIT[Marks.q1] = rdIT.countPoints
//                                        }
//                                        MarksData.selectAll().forEach { markDataIT ->
//                                            if (markDataIT[MarksData.month] == monthName) {
//                                                if (markDataIT[MarksData.login] == loginTeacher) {
//                                                    if (markDataIT[MarksData.markId] == rdIT.numEval) {
//                                                        MarksData.update({MarksData.id eq markDataIT[MarksData.id]}) { updateIT ->
//                                                            updateIT[MarksData.lastLogin] = rdIT.lastEvaluating
//                                                            updateIT[MarksData.lastDate] = rdIT.lastChangeEval
//                                                            if (rdIT.lastChangeEval != "no") {
//                                                                date = rdIT.lastChangeEval
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                    if (q2.name.replace('_', '.') == rdIT.numEval) {
//                                        Marks.update({Marks.mId eq it[Marks.mId]}) { updateIT ->
//                                            updateIT[Marks.q2] = rdIT.countPoints
//                                        }
//                                        MarksData.selectAll().forEach { markDataIT ->
//                                            if (markDataIT[MarksData.month] == monthName) {
//                                                if (markDataIT[MarksData.login] == loginTeacher) {
//                                                    if (markDataIT[MarksData.markId] == rdIT.numEval) {
//                                                        MarksData.update({MarksData.id eq markDataIT[MarksData.id]}) { updateIT ->
//                                                            updateIT[MarksData.lastLogin] = rdIT.lastEvaluating
//                                                            updateIT[MarksData.lastDate] = rdIT.lastChangeEval
//                                                            if (rdIT.lastChangeEval != "no") {
//                                                                date = rdIT.lastChangeEval
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                    if (q3.name.replace('_', '.') == rdIT.numEval) {
//                                        Marks.update({Marks.mId eq it[Marks.mId]}) { updateIT ->
//                                            updateIT[Marks.q3] = rdIT.countPoints
//                                        }
//                                        MarksData.selectAll().forEach { markDataIT ->
//                                            if (markDataIT[MarksData.month] == monthName) {
//                                                if (markDataIT[MarksData.login] == loginTeacher) {
//                                                    if (markDataIT[MarksData.markId] == rdIT.numEval) {
//                                                        MarksData.update({MarksData.id eq markDataIT[MarksData.id]}) { updateIT ->
//                                                            updateIT[MarksData.lastLogin] = rdIT.lastEvaluating
//                                                            updateIT[MarksData.lastDate] = rdIT.lastChangeEval
//                                                            if (rdIT.lastChangeEval != "no") {
//                                                                date = rdIT.lastChangeEval
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                    if (q4.name.replace('_', '.') == rdIT.numEval) {
//                                        Marks.update({Marks.mId eq it[Marks.mId]}) { updateIT ->
//                                            updateIT[Marks.q4] = rdIT.countPoints
//                                        }
//                                        MarksData.selectAll().forEach { markDataIT ->
//                                            if (markDataIT[MarksData.month] == monthName) {
//                                                if (markDataIT[MarksData.login] == loginTeacher) {
//                                                    if (markDataIT[MarksData.markId] == rdIT.numEval) {
//                                                        MarksData.update({MarksData.id eq markDataIT[MarksData.id]}) { updateIT ->
//                                                            updateIT[MarksData.lastLogin] = rdIT.lastEvaluating
//                                                            updateIT[MarksData.lastDate] = rdIT.lastChangeEval
//                                                            if (rdIT.lastChangeEval != "no") {
//                                                                date = rdIT.lastChangeEval
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                    if (q5.name.replace('_', '.') == rdIT.numEval) {
//                                        Marks.update({Marks.mId eq it[Marks.mId]}) { updateIT ->
//                                            updateIT[Marks.q5] = rdIT.countPoints
//                                        }
//                                        MarksData.selectAll().forEach { markDataIT ->
//                                            if (markDataIT[MarksData.month] == monthName) {
//                                                if (markDataIT[MarksData.login] == loginTeacher) {
//                                                    if (markDataIT[MarksData.markId] == rdIT.numEval) {
//                                                        MarksData.update({MarksData.id eq markDataIT[MarksData.id]}) { updateIT ->
//                                                            updateIT[MarksData.lastLogin] = rdIT.lastEvaluating
//                                                            updateIT[MarksData.lastDate] = rdIT.lastChangeEval
//                                                            if (rdIT.lastChangeEval != "no") {
//                                                                date = rdIT.lastChangeEval
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                    if (q6.name.replace('_', '.') == rdIT.numEval) {
//                                        Marks.update({Marks.mId eq it[Marks.mId]}) { updateIT ->
//                                            updateIT[Marks.q6] = rdIT.countPoints
//                                        }
//                                        MarksData.selectAll().forEach { markDataIT ->
//                                            if (markDataIT[MarksData.month] == monthName) {
//                                                if (markDataIT[MarksData.login] == loginTeacher) {
//                                                    if (markDataIT[MarksData.markId] == rdIT.numEval) {
//                                                        MarksData.update({MarksData.id eq markDataIT[MarksData.id]}) { updateIT ->
//                                                            updateIT[MarksData.lastLogin] = rdIT.lastEvaluating
//                                                            updateIT[MarksData.lastDate] = rdIT.lastChangeEval
//                                                            if (rdIT.lastChangeEval != "no") {
//                                                                date = rdIT.lastChangeEval
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                    if (q7.name.replace('_', '.') == rdIT.numEval) {
//                                        Marks.update({Marks.mId eq it[Marks.mId]}) { updateIT ->
//                                            updateIT[Marks.q7] = rdIT.countPoints
//                                        }
//                                        MarksData.selectAll().forEach { markDataIT ->
//                                            if (markDataIT[MarksData.month] == monthName) {
//                                                if (markDataIT[MarksData.login] == loginTeacher) {
//                                                    if (markDataIT[MarksData.markId] == rdIT.numEval) {
//                                                        MarksData.update({MarksData.id eq markDataIT[MarksData.id]}) { updateIT ->
//                                                            updateIT[MarksData.lastLogin] = rdIT.lastEvaluating
//                                                            updateIT[MarksData.lastDate] = rdIT.lastChangeEval
//                                                            if (rdIT.lastChangeEval != "no") {
//                                                                date = rdIT.lastChangeEval
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                    if (q8.name.replace('_', '.') == rdIT.numEval) {
//                                        Marks.update({Marks.mId eq it[Marks.mId]}) { updateIT ->
//                                            updateIT[Marks.q8] = rdIT.countPoints
//                                        }
//                                        MarksData.selectAll().forEach { markDataIT ->
//                                            if (markDataIT[MarksData.month] == monthName) {
//                                                if (markDataIT[MarksData.login] == loginTeacher) {
//                                                    if (markDataIT[MarksData.markId] == rdIT.numEval) {
//                                                        MarksData.update({MarksData.id eq markDataIT[MarksData.id]}) { updateIT ->
//                                                            updateIT[MarksData.lastLogin] = rdIT.lastEvaluating
//                                                            updateIT[MarksData.lastDate] = rdIT.lastChangeEval
//                                                            if (rdIT.lastChangeEval != "no") {
//                                                                date = rdIT.lastChangeEval
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                    if (q9.name.replace('_', '.') == rdIT.numEval) {
//                                        Marks.update({Marks.mId eq it[Marks.mId]}) { updateIT ->
//                                            updateIT[Marks.q9] = rdIT.countPoints
//                                        }
//                                        MarksData.selectAll().forEach { markDataIT ->
//                                            if (markDataIT[MarksData.month] == monthName) {
//                                                if (markDataIT[MarksData.login] == loginTeacher) {
//                                                    if (markDataIT[MarksData.markId] == rdIT.numEval) {
//                                                        MarksData.update({MarksData.id eq markDataIT[MarksData.id]}) { updateIT ->
//                                                            updateIT[MarksData.lastLogin] = rdIT.lastEvaluating
//                                                            updateIT[MarksData.lastDate] = rdIT.lastChangeEval
//                                                            if (rdIT.lastChangeEval != "no") {
//                                                                date = rdIT.lastChangeEval
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                    if (q10.name.replace('_', '.') == rdIT.numEval) {
//                                        Marks.update({Marks.mId eq it[Marks.mId]}) { updateIT ->
//                                            updateIT[Marks.q10] = rdIT.countPoints
//                                        }
//                                        MarksData.selectAll().forEach { markDataIT ->
//                                            if (markDataIT[MarksData.month] == monthName) {
//                                                if (markDataIT[MarksData.login] == loginTeacher) {
//                                                    if (markDataIT[MarksData.markId] == rdIT.numEval) {
//                                                        MarksData.update({MarksData.id eq markDataIT[MarksData.id]}) { updateIT ->
//                                                            updateIT[MarksData.lastLogin] = rdIT.lastEvaluating
//                                                            updateIT[MarksData.lastDate] = rdIT.lastChangeEval
//                                                            if (rdIT.lastChangeEval != "no") {
//                                                                date = rdIT.lastChangeEval
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                    if (q11.name.replace('_', '.') == rdIT.numEval) {
//                                        Marks.update({Marks.mId eq it[Marks.mId]}) { updateIT ->
//                                            updateIT[Marks.q11] = rdIT.countPoints
//                                        }
//                                        MarksData.selectAll().forEach { markDataIT ->
//                                            if (markDataIT[MarksData.month] == monthName) {
//                                                if (markDataIT[MarksData.login] == loginTeacher) {
//                                                    if (markDataIT[MarksData.markId] == rdIT.numEval) {
//                                                        MarksData.update({MarksData.id eq markDataIT[MarksData.id]}) { updateIT ->
//                                                            updateIT[MarksData.lastLogin] = rdIT.lastEvaluating
//                                                            updateIT[MarksData.lastDate] = rdIT.lastChangeEval
//                                                            if (rdIT.lastChangeEval != "no") {
//                                                                date = rdIT.lastChangeEval
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                    if (q12.name.replace('_', '.') == rdIT.numEval) {
//                                        Marks.update({Marks.mId eq it[Marks.mId]}) { updateIT ->
//                                            updateIT[Marks.q12] = rdIT.countPoints
//                                        }
//                                        MarksData.selectAll().forEach { markDataIT ->
//                                            if (markDataIT[MarksData.month] == monthName) {
//                                                if (markDataIT[MarksData.login] == loginTeacher) {
//                                                    if (markDataIT[MarksData.markId] == rdIT.numEval) {
//                                                        MarksData.update({MarksData.id eq markDataIT[MarksData.id]}) { updateIT ->
//                                                            updateIT[MarksData.lastLogin] = rdIT.lastEvaluating
//                                                            updateIT[MarksData.lastDate] = rdIT.lastChangeEval
//                                                            if (rdIT.lastChangeEval != "no") {
//                                                                date = rdIT.lastChangeEval
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                    if (q13.name.replace('_', '.') == rdIT.numEval) {
//                                        Marks.update({Marks.mId eq it[Marks.mId]}) { updateIT ->
//                                            updateIT[Marks.q13] = rdIT.countPoints
//                                        }
//                                        MarksData.selectAll().forEach { markDataIT ->
//                                            if (markDataIT[MarksData.month] == monthName) {
//                                                if (markDataIT[MarksData.login] == loginTeacher) {
//                                                    if (markDataIT[MarksData.markId] == rdIT.numEval) {
//                                                        MarksData.update({MarksData.id eq markDataIT[MarksData.id]}) { updateIT ->
//                                                            updateIT[MarksData.lastLogin] = rdIT.lastEvaluating
//                                                            updateIT[MarksData.lastDate] = rdIT.lastChangeEval
//                                                            if (rdIT.lastChangeEval != "no") {
//                                                                date = rdIT.lastChangeEval
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//            }
//
//
//            println("{EQ1")
//
//
//            transaction {
////                addLogger(StdOutSqlLogger)
//                SchemaUtils.setSchema(Schema("dtbase"))
//
//                Marks.selectAll().forEach {
//                    if (it[Marks.monthName] == monthName) {
//                        if (it[Marks.userLogin] == loginTeacher) {
//                            var evaluation = Evaluation()
//
//                            evaluation.numEval = q1.name.replace('_', '.')
//                            evaluation.countPoints = it[q1]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q2.name.replace('_', '.')
//                            evaluation.countPoints = it[q2]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q3.name.replace('_', '.')
//                            evaluation.countPoints = it[q3]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q4.name.replace('_', '.')
//                            evaluation.countPoints = it[q4]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q5.name.replace('_', '.')
//                            evaluation.countPoints = it[q5]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q6.name.replace('_', '.')
//                            evaluation.countPoints = it[q6]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q7.name.replace('_', '.')
//                            evaluation.countPoints = it[q7]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q8.name.replace('_', '.')
//                            evaluation.countPoints = it[q8]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q9.name.replace('_', '.')
//                            evaluation.countPoints = it[q9]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q10.name.replace('_', '.')
//                            evaluation.countPoints = it[q10]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q11.name.replace('_', '.')
//                            evaluation.countPoints = it[q11]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            evaluation = evaluation.copy()
//                            evaluation.numEval = q12.name.replace('_', '.')
//                            evaluation.countPoints = it[q12]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation)
//                                } else evaluationList += null
//                            }
//
//                            var evaluation13 = Evaluation()
//                            evaluation13.numEval = q13.name.replace('_', '.')
//                            evaluation13.countPoints = it[q13]
//                            MarksData.selectAll().forEach { markDataIT ->
//                                if (markDataIT[MarksData.month] == monthName) {
//                                    if (markDataIT[MarksData.login] == loginTeacher) {
//                                        if (markDataIT[MarksData.markId] == evaluation.numEval) {
//                                            evaluation.lastEvaluating = markDataIT[MarksData.lastLogin]
//                                            evaluation.lastChangeEval = markDataIT[MarksData.lastDate]
//                                        }
//                                    }
//                                }
//                            }
//                            User.select { User.login eq username }.forEach { selIT ->
//                                if (selIT[User.statusId] == 1 || selIT[User.statusId] == 3 || evaluation13.numEval in getListOfLogin(username)) {
//                                    evaluationList.add(evaluation13)
//                                } else evaluationList += null
//                            }
//
//                            ques1 = it[q1]
//                            ques2 = it[q2]
//                            ques3 = it[q3]
//                            ques4 = it[q4]
//                            ques5 = it[q5]
//                            ques6 = it[q6]
//                            ques7 = it[q7]
//                            ques8 = it[q8]
//                            ques9 = it[q9]
//                            ques10 = it[q10]
//                            ques11 = it[q11]
//                            ques12 = it[q12]
//                            ques13 = it[q13]
//
//                        }
//                    }
//                }
//            }
//
//            transaction {
//                SchemaUtils.setSchema(Schema("dtbase"))
//                Logs.insert { log ->
//                    log[Logs.date] = date
//                    log[Logs.apprName] = username
//                    log[Logs.userName] = loginTeacher.toString()
//                    log[Logs.l1] = ques1
//                    log[Logs.l2] = ques2
//                    log[Logs.l3] = ques3
//                    log[Logs.l4] = ques4
//                    log[Logs.l5] = ques5
//                    log[Logs.l6] = ques6
//                    log[Logs.l7] = ques7
//                    log[Logs.l8] = ques8
//                    log[Logs.l9] = ques9
//                    log[Logs.l10] = ques10
//                    log[Logs.l11] = ques11
//                    log[Logs.l12] = ques12
//                    log[Logs.l13] = ques13
//                }
//            }
//
//
//            println(evaluationList)
//            call.respond(evaluationList)
//
//        }
//
//    }
//}