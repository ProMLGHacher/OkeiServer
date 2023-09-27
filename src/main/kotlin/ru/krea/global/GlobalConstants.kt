package ru.krea.global

import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.FileReader


const val IMAGES_LINK = "http://176.28.64.201:3434/images/"
const val PREMIUM_REPORTS_LINK = "http://192.168.101.25:3434/reports/stat/"

const val PREMIUM_REPORTS_PATH = "C:\\Users\\SU\\Desktop\\reports\\rep\\"
const val PREMIUM_LOGS_PATH = "C:\\Users\\SU\\Desktop\\reports\\logs\\"

const val MAX_MARK_VALUE_FOR_TEACHER = 54

val MONTHS_NAMES = listOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")

fun csvToXLSX() {
    try {
        val csvFileAddress = "C:\\Users\\7\\Desktop\\iuhdfuivbiuvwdnkvhsdiuvnsoifisvihvuywev.csv" //csv file address
        val xlsxFileAddress = "C:\\Users\\7\\Desktop\\test.xlsx" //xlsx file address
        val workBook = XSSFWorkbook()
        val sheet: XSSFSheet = workBook.createSheet("sheet1")
        var currentLine: String?
        var RowNum = 0
        val br = BufferedReader(FileReader(csvFileAddress))
        while (br.readLine().also { currentLine = it } != null) {
            val str = currentLine!!.split(",".toRegex()).toTypedArray()
            RowNum++
            val currentRow: XSSFRow = sheet.createRow(RowNum)
            for (i in str.indices) {
                currentRow.createCell(i).setCellValue(str[i])
            }
        }
        val fileOutputStream = FileOutputStream(xlsxFileAddress)
        workBook.write(fileOutputStream)
        fileOutputStream.close()
        println("Done")
    } catch (ex: Exception) {
        println(ex.message + "Exception in try")
    }
}

