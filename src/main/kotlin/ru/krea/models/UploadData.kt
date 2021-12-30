package ru.krea.models

import kotlinx.serialization.Serializable

@Serializable
data class UploadData(
    var nameMonth: String,
    var loginTeacher: String,
    var list: List<Evaluation?>
)
