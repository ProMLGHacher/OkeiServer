package ru.krea.models

import kotlinx.serialization.Serializable
import ru.krea.models.user.PremiumTeacher

@Serializable
data class Report(
    val canRecalculation : Boolean = false, //Возможность перерасчёта
    val urlAccount: String = "", //ссылка на файл отчёта excel
    val totalAmountPremium : String = "500000", //Общая сумма премии
    val fixedPremium : String = "2000", //Фиксированная премия
    val totalAmountPoints: String = "1728", //Общее количество баллов
    val partSemiannualPremium: String = "100000", //Часть полугодовой премии
    val distributablePremium: String = "200000",//распределяемая премия
    val pointValue: String = "250",//Стоимость балла
    val listPremiumTeacher: List<PremiumTeacher> = listOf(PremiumTeacher("Никола", "54", "15000"),
        PremiumTeacher("Олег", "600", "130000"), PremiumTeacher("Мерлан", "0", "0")
    )
)
