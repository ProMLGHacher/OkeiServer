package ru.krea.models.user

import kotlinx.serialization.Serializable

@Serializable
data class Teacher (
    var name: String = "",
    var login: String = "",
    var countPoint: Int = 0,
    var lastChange: String = "",
    var isKing: Boolean = false
)