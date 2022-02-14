package com.example.cool_app.model.repository

import android.content.Context
import com.example.cool_app.model.entities.Currency
import com.example.cool_app.model.entities.IncCurrency
import com.example.cool_app.model.database.CurrencyDatabase

class InternalRepository: CurrenciesRepository {

    override suspend fun getIncCurrencies(context: Context?): MutableList<IncCurrency> {
        val incCurrenciesStr = CurrencyDatabase().loadIncCurrencies(context!!) ?: ""
        val incCurrenciesListStr = parseSavedCurrencies(incCurrenciesStr)
        return parseListOfIncCurrencies(incCurrenciesListStr)
    }

    override suspend fun getCurrencies(context: Context?): MutableList<Currency> {
        val currenciesStr = CurrencyDatabase().loadCurrencies(context!!) ?: ""
        val currenciesListStr = parseSavedCurrencies(currenciesStr)
        return parseListOfCurrencies(currenciesListStr)
    }

    override suspend fun getAveragePercent(context: Context?): Double {
        return CurrencyDatabase().loadAveragePercent(context!!)?.toDouble() ?: -1.0
    }

    override suspend fun getMaxIncCurrency(context: Context?): IncCurrency {
        val  maxIncCurrencyStr = CurrencyDatabase().loadMaxIncCurrency(context!!) ?: ""
        return parseIncCurrency(maxIncCurrencyStr)
    }

    override suspend fun getMinIncCurrency(context: Context?): IncCurrency {
        val  minIncCurrencyStr = CurrencyDatabase().loadMinIncCurrency(context!!) ?: ""
        return parseIncCurrency(minIncCurrencyStr)
    }

    override suspend fun getDate(context: Context?): String {
        return CurrencyDatabase().loadDate(context!!) ?: ""
    }
}