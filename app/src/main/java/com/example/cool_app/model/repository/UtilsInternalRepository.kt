package com.example.cool_app.model.repository

import com.example.cool_app.model.entities.Currency
import com.example.cool_app.model.entities.IncCurrency

internal fun parseSavedCurrencies(currencies: String): MutableList<String> {
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

internal fun parseIncCurrency(incCurrencyStr: String): IncCurrency {
    val incCurrency  = IncCurrency()
    val indexPercent = incCurrencyStr.indexOf("+")
    val indexCode = incCurrencyStr.indexOf("[")
    val size = incCurrencyStr.length
    incCurrency.name = incCurrencyStr.substring(0,indexPercent-1)
    incCurrency.percentageInc = incCurrencyStr.substring(indexPercent+1,indexCode-1).toDouble()
    incCurrency.charCode = incCurrencyStr.substring(indexCode+1,size-1)
    return incCurrency
}

internal fun parseCurrency(currencyStr: String): Currency {
    val currency  = Currency()
    val indexValue = currencyStr.indexOf("+")
    val indexCode = currencyStr.indexOf("[")
    val size = currencyStr.length
    currency.name = currencyStr.substring(0,indexValue-1)
    currency.value = currencyStr.substring(indexValue,indexCode-1).toDouble()
    currency.charCode = currencyStr.substring(indexCode+1,size-1)
    return currency
}

internal fun parseListOfIncCurrencies(currencies: MutableList<String>): MutableList<IncCurrency> {
    val increasedCurrenciesList: MutableList<IncCurrency> = mutableListOf()
    currencies.forEach{ increasedCurrenciesList.add(parseIncCurrency(it))}
    return increasedCurrenciesList
}

internal fun parseListOfCurrencies(currencies: MutableList<String>): MutableList<Currency> {
    val currenciesList: MutableList<Currency> = mutableListOf()
    currencies.forEach{ currenciesList.add(parseCurrency(it))}
    return currenciesList
}