package com.example.cool_app

import android.content.Context
import android.net.ConnectivityManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

/******************************************************************************
 *                              DATE FUNCTIONS                                *
 ******************************************************************************/

// Сегодняшняя дата
internal fun dateToday(): Date {
    val dateToday = Date()
    dateToday.hours = 0
    dateToday.minutes = 0
    dateToday.seconds = 0
    return dateToday
}

// Парсинг даты из подготовленной строки
internal fun stringToDate(date: String, format: String = "dd.MM.yyyy"): Date {
    val formatter = SimpleDateFormat(format)
    return formatter.parse(date)
}

// Получение даты прошлого месяца
// (функцию нужно сделать более абстрактной, чтобы можно было получать любою дату относительно стартовой)



/******************************************************************************
 *                              OTHERS FUNCTIONS                              *
 ******************************************************************************/

// Проверка наличия интернета
internal fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.activeNetworkInfo
    return netInfo != null && netInfo.isConnectedOrConnecting
}

// Округление числа типа Double с оперделённой точностью
internal infix fun Double.roundDouble(point: Int): Double {
    return when {
        point > 0 -> (this * 10.0.pow(point.toDouble())).roundToInt()
            .toDouble() / 10.0.pow(point.toDouble())
        point == 0 -> this.roundToInt().toDouble()
        else -> 0.0
    }
}

fun parseSavedCurrencies(currencies: String): MutableList<String> {
    var string = currencies.replace("\n", "#")
    val listOfCurrencies: MutableList<String> = mutableListOf()
    while (true) {
        val index = string.indexOf("#")
        val size = string.length
        if (index != -1) {
            listOfCurrencies.add(string.substring(0, index))
            string = string.substring(index + 1, size)
        } else break
    }
    return listOfCurrencies
}