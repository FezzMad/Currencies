package com.example.cool_app.model.entities


//  Информация по дате
internal data class DateAPI(
    val day: Int,
    val month: Int,
    val year: Int
)

//  Информация, получаемая после перебора валют:
//  список валют, цена которых возросла,
//  средний процент роста,
//  номер валюты в списке с максимальным процентом роста,
//  номер валюты в списке с минимальным процентом роста
internal data class IncCurrenciesProp(
    val incCurrencies: MutableList<IncCurrency> = mutableListOf(),
    val averagePercent: Double? = null,
    var indexMax: Int = 0,
    var indexMin: Int = 0,
)

//  Информация о валюте, которая повысилась в цене
data class IncCurrency(
    var charCode: String? = null,
    var name: String? = null,
    var percentageInc: Double? = null //  процент повышения цены
)

//  Информация о валюте
data class Currency(
    var charCode: String? = null,
    var name: String? = null,
    var value: Double? = null
)

internal data class IncCurrenciesResult(
    val date: String?,
    val result: String,
    val list: MutableList<String>,
    val incCurrencies: String,
    var numbOfMoreTwoPerc: Int = 0)

internal data class CurrenciesResult(
    val stringCurrencies: MutableList<String>,
    val currencies: MutableList<Currency>,
    val listSave: String
)

internal data class EmptyData(
    val date: String = "Empty",
    val result: String = "Average percent: empty" +
            "Max: empty\n" +
            "Min: empty",
    val incCurrencies: MutableList<IncCurrency> = mutableListOf(
        IncCurrency("NULL","NULL",-1.0),
        IncCurrency("NULL","NULL",-1.0),
        IncCurrency("NULL","NULL",-1.0),
        IncCurrency("NULL","NULL",-1.0))
)