package com.example.cool_app

import com.google.gson.annotations.SerializedName

//  Значения в принятом xml-файле
internal data class CBAPI(@SerializedName("ValCurs") var valCurs: ValCurs? = ValCurs())

//  Значения в принятом xml-файле
internal data class ValCurs(
    @SerializedName("Date") var date: String? = null,
    @SerializedName("Valute") var currency: MutableList<Valute> = mutableListOf(),
    @SerializedName("name") var name: String? = null
)

//  Значения в принятом xml-файле
internal data class Valute(
    @SerializedName("CharCode") var charCode: String? = null,
    @SerializedName("ID") var id: String? = null,
    @SerializedName("Name") var name: String? = null,
    @SerializedName("Nominal") var nominal: String? = null,
    @SerializedName("NumCode") var numCode: String? = null,
    @SerializedName("Value") var value: String? = null,
)

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
internal data class IncreasedCurrenciesProperties(
    val increasedCurrenciesList: MutableList<IncreasedCurrencies> = mutableListOf(),
    val averagePercent: Double? = null,
    var indexMax: Int = 0,
    var indexMin: Int = 0,
)

//  Информация о валюте, которая повысилась в цене
internal data class IncreasedCurrencies(
    var charCode: String? = null,
    var name: String? = null,
    var percentageIncrease: Double? = null //  процент повышения цены
)

internal data class Result(
    val date: String?,
    val result: String,
    val list: MutableList<String>,
    val incCurrencies: String)