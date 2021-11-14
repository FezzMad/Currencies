package com.example.cool_app

import android.content.Context
import android.net.ConnectivityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

/******************************************************************************
 *                              DATE FUNCTIONS                                *
 ******************************************************************************/

// Проверка актуальности данных
internal fun isActualDate(date: Date): Boolean {
    val dateActual = dateToday()
    return !(date.before(dateActual) && date.toString() != dateActual.toString())
}

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
internal suspend fun getDateLastMonth(exchangeRate: CBAPI): DateAPI =
    withContext(Dispatchers.Default) {
        val dateFromAPI = exchangeRate.valCurs?.date
        val date = if (dateFromAPI != null) stringToDate(dateFromAPI)
        else {
            // дата не получена, нужно что-то с этим сделать
            dateToday() // пока пусть будет дата сегодняшнего дня, значит валюты не повысятся в цене
        }
        val calendar: Calendar = GregorianCalendar(date.year, date.month, date.date)
        calendar.add(Calendar.MONTH, -1)
        val month = (calendar.get(Calendar.MONTH) + 1)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val year = (calendar.get(Calendar.YEAR) + 1900)
        return@withContext DateAPI(day, month, year)
    }

/******************************************************************************
 *                              API FUNCTIONS                                 *
 ******************************************************************************/

// Парсинг xml-файла
// (пока просто работает, нужно доработать)
internal suspend fun parseCBPAPI(xmlString: String): CBAPI {
    val length = xmlString.length
    var counter = 0
    var str = ""
    var valuteCounter = 0
    println(length)
    val mutListOfValute: MutableList<String> = mutableListOf()
    var valuteInd: Int? = null
    var valuteEndInd: Int? = null
    while (counter <= length) {
        if (counter + 7 < length)
            str = xmlString.substring(counter, counter + 7)
        counter++

        if (str == "<Valute") {
            valuteCounter++
            valuteInd = counter
        }
        if (str == "</Valut") {
            valuteEndInd = counter
            if (valuteInd != null && valuteEndInd != null) {
                mutListOfValute.add(xmlString.substring(valuteInd - 1, valuteEndInd + 8))
            }
        }

    }
    val cbapi = CBAPI()
    val dataInd = xmlString.indexOf("Date")
    println(xmlString.substring(dataInd + 6, dataInd + 16))
    cbapi.valCurs?.date = xmlString.substring(dataInd + 6, dataInd + 16)
    mutListOfValute.forEach {
        val valute = Valute()
        valute.charCode =
            it.substring(it.indexOf("<CharCode>") + "<CharCode>".length, it.indexOf("</CharCode>"))
        valute.numCode =
            it.substring(it.indexOf("<NumCode>") + "<NumCode>".length, it.indexOf("</NumCode>"))
        valute.name = it.substring(it.indexOf("<Name>") + "<Name>".length, it.indexOf("</Name>"))
        valute.nominal =
            it.substring(it.indexOf("<Nominal>") + "<Nominal>".length, it.indexOf("</Nominal>"))
        valute.value =
            it.substring(it.indexOf("<Value>") + "<Value>".length, it.indexOf("</Value>"))
        valute.id = it.substring(it.indexOf("ID=\"") + "ID=\"".length, it.indexOf("\">"))
        cbapi.valCurs?.currency?.add(valute)

    }
    return cbapi
}

// Перебор валют, возвращает данные о валютах, которые повысились в цене
internal suspend fun getIncreasedCurrencies(
    exchangeRateBefore: CBAPI,
    exchangeRateAfter: CBAPI
): IncreasedCurrenciesProperties = withContext(Dispatchers.Default) {
    val increasedCurrenciesList: MutableList<IncreasedCurrencies> =
        mutableListOf() // Лист возросших валют
    var index = -1  // Индекс валюты в листе
    var indexMax = 0  // Индекс валюты с максимальным приростом
    var indexMin = 0  // Индекс валюты с минимальным приростом
    var maxIncrease = 0.0  // Максимальное значение прироста в %
    var minIncrease = 101.0  // Минимальное значение прироста в %
    var amount = 0.0  // Сумма всех процентных приростов
    for (valute in exchangeRateBefore.valCurs?.currency ?: mutableListOf()) {
        val valueToDay =
            valute.value?.replace(",", ".")?.toDouble() ?: 0.0 // Курс валюты на сегодня
        val valueLastMonth =
            exchangeRateAfter.valCurs?.currency?.find { it.charCode == valute.charCode }?.value?.replace(
                ",",
                "."
            )
                ?.toDouble() ?: 0.0 // Курс валюты за прошлый месяц
        val difference = valueToDay - valueLastMonth  // Разница курсов
        if (difference > 0.0) {
            index++
            val onePercent = valueLastMonth / 100.0  // 1% от курса валюты за прошлый месяц
            val percent = (onePercent * difference).roundDouble(3)  //  Процент роста валюты
            //** Заполнение информации о валюте */
            val increasedCurrencies = IncreasedCurrencies()
            increasedCurrencies.charCode = valute.charCode
            increasedCurrencies.name = valute.name
            increasedCurrencies.percentageIncrease = percent
            increasedCurrenciesList.add(increasedCurrencies)
            amount += percent
            //** Определение валюты с максимальным приростом */
            if (percent > maxIncrease) {
                indexMax = index
                maxIncrease = percent
            }
            ///** Определение валюты с минимальным приростом */
            if (percent < minIncrease) {
                indexMin = index
                minIncrease = percent
            }
        }
    }
    return@withContext if (increasedCurrenciesList.isNotEmpty()) {
        val averagePercent =
            (amount / (index + 1)).roundDouble(3)  // Подсчёт среднего процента повышения стоимости
        IncreasedCurrenciesProperties(increasedCurrenciesList, averagePercent, indexMax, indexMin)
    } else IncreasedCurrenciesProperties()
}

// Получение информации о курсе валют с Центрального банка на указанную дату
// (на сегодня, если дата не указана)
internal suspend fun getCenterBankExchangeRate(
    day: Int? = null,
    month: Int? = null,
    year: Int? = null
): CBAPI = withContext(Dispatchers.IO) {
    val date = if (day != null && month != null && year != 0) {
        val dayStr = if (day.toString().length == 1) "0$day" else day.toString()
        val monthStr = if (month.toString().length == 1) "0$month" else month.toString()
        "?date_req=$dayStr/$monthStr/$year"
    } else ""
    var exchangeRate = CBAPI()
    try {
        val xmlString = URL("https://www.cbr.ru/scripts/XML_daily_eng.asp$date").readText()
        exchangeRate = parseCBPAPI(xmlString)
    } catch (e: FileNotFoundException) {
        //** Ошибка */
    }
    exchangeRate
}


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