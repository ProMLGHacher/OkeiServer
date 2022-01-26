package ru.krea.models.criterion

import kotlinx.serialization.Serializable

@Serializable
data class VoteCriterion (
    val id: String,
    var nameAppraiser: String,
    var lastChange: String,
    var points: Int,
)