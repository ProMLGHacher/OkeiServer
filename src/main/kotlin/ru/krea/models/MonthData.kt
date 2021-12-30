package ru.krea.models

import kotlinx.serialization.Serializable

@Serializable
data class MonthData(
    var nameMonth: String = "",
    var lastChange: String = "В разработке",
    var leader: String = "Пока нет лидера",
    var underway: Boolean? = null,
    var progress: Float = 0f
)
