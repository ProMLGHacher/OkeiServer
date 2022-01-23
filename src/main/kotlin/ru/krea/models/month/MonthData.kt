package ru.krea.models.month

data class MonthData (
    var name: String = "Месяц",
    var underway : Boolean = false, //Идёт ли сейчас = true, если нет = false(месяц прошел),
    var lastChange : String = "",
    var progress : Float = 0f,
    val topTeachers: MutableList<String> = mutableListOf("лидера нет", "лидера нет", "лидера нет", "лидера нет", "лидера нет") // учителя должны быть отфильтрованными
)