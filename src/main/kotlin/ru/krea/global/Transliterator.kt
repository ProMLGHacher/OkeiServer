package ru.krea.global

fun encode(toEncode: String): String {
    var latyn = toEncode
    if (toEncode.equals("а", ignoreCase = true)) latyn = "a"
    if (toEncode.equals("б", ignoreCase = true)) latyn = "b"
    if (toEncode.equals("в", ignoreCase = true)) latyn = "v"
    if (toEncode.equals("г", ignoreCase = true)) latyn = "g"
    if (toEncode.equals("д", ignoreCase = true)) latyn = "d"
    if (toEncode.equals("е", ignoreCase = true)) latyn = "e"
    if (toEncode.equals("ж", ignoreCase = true)) latyn = "zh"
    if (toEncode.equals("з", ignoreCase = true)) latyn = "z"
    if (toEncode.equals("и", ignoreCase = true)) latyn = "i"
    if (toEncode.equals("й", ignoreCase = true)) latyn = "j"
    if (toEncode.equals("к", ignoreCase = true)) latyn = "k"
    if (toEncode.equals("л", ignoreCase = true)) latyn = "l"
    if (toEncode.equals("м", ignoreCase = true)) latyn = "m"
    if (toEncode.equals("н", ignoreCase = true)) latyn = "n"
    if (toEncode.equals("о", ignoreCase = true)) latyn = "o"
    if (toEncode.equals("п", ignoreCase = true)) latyn = "p"
    if (toEncode.equals("р", ignoreCase = true)) latyn = "r"
    if (toEncode.equals("с", ignoreCase = true)) latyn = "s"
    if (toEncode.equals("т", ignoreCase = true)) latyn = "t"
    if (toEncode.equals("у", ignoreCase = true)) latyn = "u"
    if (toEncode.equals("ф", ignoreCase = true)) latyn = "f"
    if (toEncode.equals("х", ignoreCase = true)) latyn = "x"
    if (toEncode.equals("ц", ignoreCase = true)) latyn = "c"
    if (toEncode.equals("ч", ignoreCase = true)) latyn = "ch"
    if (toEncode.equals("ш", ignoreCase = true)) latyn = "sh"
    if (toEncode.equals("щ", ignoreCase = true)) latyn = "shh"
    if (toEncode.equals("ы", ignoreCase = true)) latyn = "y"
    if (toEncode.equals("э", ignoreCase = true)) latyn = "e"
    if (toEncode.equals("ю", ignoreCase = true)) latyn = "yu"
    if (toEncode.equals("я", ignoreCase = true)) latyn = "ya"
    return latyn
}

fun stringTolatyn(text: String): String {
    var newText = ""
    var selectedChar: String
    var convertedChar: String
    for (i in 0 until text.length) {
        //Select the next character
        selectedChar = text[i].toString() + ""

        // Convert the character
        convertedChar = encode(selectedChar)
        if (convertedChar.equals("", ignoreCase = true)) // "|" separates each word represented in latyn code
        {
            newText = "$newText " //создер арасындагы танба
        } else {
            newText = newText + convertedChar
            if (!convertedChar.equals("", ignoreCase = true)) {
                newText = newText + ""
            }
        }
    }
    return newText
}