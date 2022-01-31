package ru.krea.models

import kotlinx.serialization.Serializable
import ru.krea.models.user.PremiumTeacher

@Serializable
data class Report(
    var urlExcel: String = "", //ссылка на файл отчёта excel
    var totalAmountPremium : String = "500000", //Общая сумма премии
    var fixedPremium : String = "2000", //Фиксированная премия
    var totalAmountPoints: String = "1728", //Общее количество баллов
    var partSemiannualPremium: String = "100000", //Часть полугодовой премии
    var distributablePremium: String = "200000",//распределяемая премия
    var pointValue: String = "250",//Стоимость балла
    var listPremiumTeacher: List<PremiumTeacher> = listOf(PremiumTeacher("Никола", "54", "15000"),
        PremiumTeacher("Олег", "600", "130000"), PremiumTeacher("Мерлан", "0", "0")
    )
)
