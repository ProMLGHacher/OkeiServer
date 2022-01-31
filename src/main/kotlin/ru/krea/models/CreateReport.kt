package ru.krea.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateReport(
    val totalAmountPremium : String, //Общая сумма премии
    val fixedPremium : String, //Фиксированная премия
    val totalAmountPoints: String, //Общее количество баллов
    val partSemiannualPremium: String, //Часть полугодовой премии
    val distributablePremium: String,//распределяемая премия
    val pointValue: String, //Стоимость балла
)
