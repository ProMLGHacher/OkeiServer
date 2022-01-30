package ru.krea.models

import kotlinx.serialization.Serializable

@Serializable
data class ValuesCalculatingMonth(
    val totalAmountPoints: String = "1290",
    val countTeacher: Int = 48
)
