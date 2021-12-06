package com.example.cool_app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.net.URL
import java.util.*

/******************************************************************************
 *                              API FUNCTIONS                                 *
 ******************************************************************************/

// Парсинг xml-файла
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

//подготовка информации о Increased currencies для вывода на экран
internal suspend fun buildResult(
    increasedCurrenciesProperties: IncreasedCurrenciesProperties,
    dateAPI: DateAPI,
    exchangeRateToDay: CBAPI,
    number: Double
): IncExRateResult = withContext(Dispatchers.Default) {
    val dateSaved: String?
    val result: String?
    var incCurrencies: String?
    var counter: Int = 0
    val list: MutableList<String> = mutableListOf()
    return@withContext if (increasedCurrenciesProperties.increasedCurrenciesList.isNotEmpty()) {
        val maxValue =
            increasedCurrenciesProperties.increasedCurrenciesList[increasedCurrenciesProperties.indexMax]
        val minValue =
            increasedCurrenciesProperties.increasedCurrenciesList[increasedCurrenciesProperties.indexMin]
        dateSaved = exchangeRateToDay.valCurs?.date
        result =
            "Average percent +${increasedCurrenciesProperties.averagePercent}\n" +
                    "${maxValue.name} +${maxValue.percentageIncrease} (${maxValue.charCode})\n" +
                    "${minValue.name} +${minValue.percentageIncrease} (${minValue.charCode})"
        incCurrencies = ""
        increasedCurrenciesProperties.increasedCurrenciesList.forEach {
            incCurrencies += "${it.name} +${it.percentageIncrease} (${it.charCode})\n"
            list.add("${it.name} +${it.percentageIncrease} (${it.charCode})")
        }
        increasedCurrenciesProperties.increasedCurrenciesList.forEach {
            if (it.percentageIncrease ?: 0.0 > number) counter++
        }
        IncExRateResult(dateSaved, result, list, incCurrencies, counter)
    } else {
        dateSaved =
            "${dateAPI.day}.${dateAPI.month}.${dateAPI.year}"
        result = "Нет валют, которые бы повысились в цене"
        incCurrencies = ""
        IncExRateResult(dateSaved, result, list, incCurrencies)
    }
}

//Получение информации о Increased currencies
internal suspend fun getIncExRate(number: Double = 2.0): IncExRateResult = withContext(Dispatchers.IO) {
    val exchangeRateToDay = async { getCenterBankExchangeRate() }
    val dateAPI = async { getDateLastMonth(exchangeRateToDay.await()) }
    val exchangeRateLastMonth = async {
        getCenterBankExchangeRate(
            dateAPI.await().day,
            dateAPI.await().month,
            dateAPI.await().year
        )
    }
    val increasedCurrenciesProperties = async {
        getIncreasedCurrencies(
            exchangeRateToDay.await(),
            exchangeRateLastMonth.await()
        )
    }
    val resultObj = async {
        buildResult(
            increasedCurrenciesProperties.await(),
            dateAPI.await(),
            exchangeRateToDay.await(),
            number
        )
    }
    return@withContext resultObj.await()
}

//Получение информации о курсе валют
internal suspend fun getExRate(): ExRateResult = withContext(Dispatchers.IO) {
    val list: MutableList<String> = mutableListOf()
    getCenterBankExchangeRate().valCurs?.currency?.forEach { list.add("${it.name} ${it.value} ${it.charCode}") }
    //date = stringToDate(getCenterBankExchangeRate().valCurs?.date ?: dateToday().toString())
    var listSave = ""
    list.forEach { listSave += "$it\n" }
    return@withContext ExRateResult(list,listSave)
}

//Получение даты на месяц назад
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
