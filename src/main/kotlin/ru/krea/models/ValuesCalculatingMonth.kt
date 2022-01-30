package ru.krea.models

import kotlinx.serialization.Serializable

@Serializable
data class ValuesCalculatingMonth(
    val totalAmountPoints: String,
    val countTeacher: Int
)
