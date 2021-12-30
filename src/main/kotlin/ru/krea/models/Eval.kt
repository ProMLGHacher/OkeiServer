package ru.krea.models

import kotlinx.serialization.Serializable

@Serializable
data class Eval(
    var list: List<Evaluation?>
)