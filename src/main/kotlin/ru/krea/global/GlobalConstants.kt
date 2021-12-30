package ru.krea.global

const val IMAGES_PATH = "http://176.28.64.201:3434/images/"

val MONTHS_NAMES = listOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")

fun getListOfLogin(login: String) = when (login) {
    in "semenova08" -> listOf("1.1", "2.1", "2.2", "2.3", "3.1", "3.2", "3.3")
    in listOf("malyshev04", "shavel09", "balysheva03") -> listOf("1.2", "4.1", "4.2", "5.1", "5.2", "5.3")
    "nashchekina05" -> listOf("1.1", "1.2", "5.1", "5.2", "5.3")
    "vysochin08" -> listOf("2.1", "2.2", "2.3")
    "volzhentseva01" -> listOf("4.1", "4.2")
    "myazina09" -> listOf("1.1", "1.2", "2.1", "2.2", "2.3", "3.1", "3.2", "3.3", "4.1", "4.2", "5.1", "5.2", "5.3")
    else -> listOf("")
}