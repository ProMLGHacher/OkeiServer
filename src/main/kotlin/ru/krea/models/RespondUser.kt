package ru.krea.models

import kotlinx.serialization.Serializable

@Serializable
data class RespondUser(
    var login: String = "",
    var name: String = "",
    var imageURL: String = "",
    var status: String = "",
    var token: String = "")