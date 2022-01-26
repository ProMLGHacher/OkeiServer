package ru.krea.models.user

import kotlinx.serialization.Serializable

@Serializable
data class RespondUser(
    var login: String = "",
    var name: String = "",
    var status: String = "",
    var token: String = ""
)