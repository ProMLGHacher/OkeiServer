package ru.krea.models.user

import kotlinx.serialization.Serializable

@Serializable
data class UserAuthData(val login: String,
                        val password: String)
