package com.example.cool_app.model.api

import com.example.cool_app.dateToday
import com.example.cool_app.model.entities.*
import com.example.cool_app.model.entities.Currency
import com.example.cool_app.model.entities.FullCurrency
import com.example.cool_app.model.entities.DateAPI
import com.example.cool_app.model.entities.CurrenciesResult
import com.example.cool_app.model.entities.FullAnswer
import com.example.cool_app.model.entities.IncCurrenciesProp
import com.example.cool_app.roundDouble
import com.example.cool_app.stringToDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.util.*


/******************************************************************************
 *                              API FUNCTIONS                                 *
 ******************************************************************************/



// Парсинг xml-файла в объект FullAnswer
internal fun parseXmlFromAPI(xmlString: String): FullAnswer {
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
    val cbapi = FullAnswer()
    val dataInd = xmlString.indexOf("Date")
    cbapi.valCurs?.date = xmlString.substring(dataInd + 6, dataInd + 16)
    mutListOfValute.forEach {
        val valute = FullCurrency()
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
        cbapi.valCurs?.fullCurrencies?.add(valute)
    }
    return cbapi
}

// Возвращает список валют, которые повысились в цене
//  средний процент роста,
//  индексы в списке валют наименьшего и наибольшего роста
internal suspend fun getIncCurrenciesProp(
    currenciesBefore: FullAnswer,
    currenciesAfter: FullAnswer
): IncCurrenciesProp = withContext(Dispatchers.Default) {

    val incCurrencyList: MutableList<IncCurrency> =
        mutableListOf() // Лист возросших валют
    var index = -1  // Индекс валюты в листе
    var indexMax = 0  // Индекс валюты с максимальным приростом
    var indexMin = 0  // Индекс валюты с минимальным приростом
    var maxIncrease = 0.0  // Максимальное значение прироста в %
    var minIncrease = 101.0  // Минимальное значение прироста в %
    var amount = 0.0  // Сумма всех процентных приростов

    for (valute in currenciesBefore.valCurs?.fullCurrencies ?: mutableListOf()) {
        val valueToDay =
            valute.value?.replace(",", ".")?.toDouble() ?: 0.0 // Курс валюты на сегодня
        val valueLastMonth =
            currenciesAfter.valCurs?.fullCurrencies?.find { it.charCode == valute.charCode }?.value?.replace(
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
            val increasedCurrencies = IncCurrency()
            increasedCurrencies.charCode = valute.charCode
            increasedCurrencies.name = valute.name
            increasedCurrencies.percentageInc = percent
            incCurrencyList.add(increasedCurrencies)
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
    return@withContext if (incCurrencyList.isNotEmpty()) {
        val averagePercent =
            (amount / (index + 1)).roundDouble(3)  // Подсчёт среднего процента повышения стоимости
        IncCurrenciesProp(incCurrencyList, averagePercent, indexMax, indexMin)
    } else IncCurrenciesProp()
}

// Получение информации о курсе валют с Центрального банка на указанную дату
// (на сегодня, если дата не указана)
internal suspend fun getCurrenciesInfo(
    day: Int? = null,
    month: Int? = null,
    year: Int? = null
): FullAnswer = withContext(Dispatchers.IO) {
    val date = if (day != null && month != null && year != 0) {
        val dayStr = if (day.toString().length == 1) "0$day" else day.toString()
        val monthStr = if (month.toString().length == 1) "0$month" else month.toString()
        "$dayStr/$monthStr/$year"
    } else ""
    var exchangeRate = FullAnswer()
    try {
        val response = cbapi().getRuData(date).execute()
        val xmlString = response.body()!!.string()
        exchangeRate = parseXmlFromAPI(xmlString)
    } catch (e: FileNotFoundException) {
        //** Ошибка */
    }
    exchangeRate
}

//Получение информации о Increased currencies
internal suspend fun getIncExRate(trackPercent: Double = 2.0): IncCurrenciesProp = withContext(Dispatchers.IO) {
    val exchangeRateToDay = async { getCurrenciesInfo() }
    val dateAPI = async { getDateLastMonth(exchangeRateToDay.await()) }
    val exchangeRateLastMonth = async {
        getCurrenciesInfo(
            dateAPI.await().day,
            dateAPI.await().month,
            dateAPI.await().year
        )
    }
    val increasedCurrenciesProperties = async {
        getIncCurrenciesProp(
            exchangeRateToDay.await(),
            exchangeRateLastMonth.await()
        )
    }
    return@withContext increasedCurrenciesProperties.await()
}

//Получение информации о курсе валют
internal suspend fun getExRate(): CurrenciesResult = withContext(Dispatchers.IO) {
    val stringExRate: MutableList<String> = mutableListOf()
    val exRate: MutableList<Currency> = mutableListOf()
    val fullCurrencies = getCurrenciesInfo().valCurs?.fullCurrencies
    fullCurrencies?.forEach { stringExRate.add("${it.name} ${it.value} ${it.charCode}") }
    for (fullCurrency in fullCurrencies ?: mutableListOf()) {
        val currency = Currency()
        currency.charCode = fullCurrency.charCode
        currency.name = fullCurrency.name
        currency.value = fullCurrency.value?.replace(",", ".")?.toDouble()
        exRate.add(currency)
    }
    var listSave = ""
    stringExRate.forEach { listSave += "$it\n" }

    return@withContext CurrenciesResult(stringExRate,exRate,listSave)
}

//Получение даты на месяц назад
internal suspend fun getDateLastMonth(exchangeRate: FullAnswer): DateAPI =
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
