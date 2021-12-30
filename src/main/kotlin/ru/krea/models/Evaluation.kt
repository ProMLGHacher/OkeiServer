package ru.krea.models

import kotlinx.serialization.Serializable

@Serializable
data class Evaluation(
    var numEval: String = "",
    var lastChangeEval: String = "",
    var lastEvaluating: String = "",
    var countPoints: Int = 0
)
