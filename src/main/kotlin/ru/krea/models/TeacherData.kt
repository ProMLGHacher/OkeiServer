package ru.krea.models

import kotlinx.serialization.Serializable

@Serializable
data class TeacherData(
    var login:String = "",
    var name: String = "",
    var lastChangeTeacher: String = "В разработке",
    var countPoints: Int = 0
)
