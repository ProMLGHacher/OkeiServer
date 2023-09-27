package ru.krea.models.user

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    var login: String = "",
    var name: String = "",
    var status: String = ""
)