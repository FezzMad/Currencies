package com.example.cool_app.model.repository

import android.content.Context
import com.example.cool_app.model.entities.Currency
import com.example.cool_app.model.entities.IncCurrency
import com.example.cool_app.model.api.getCurrenciesInfo
import com.example.cool_app.model.api.getExRate
import com.example.cool_app.model.api.getIncExRate

class ExternalRepository: CurrenciesRepository {

//    companion object {
//        internal suspend fun main(): IncCurrenciesProp{
//            return getIncExRate()
//        }
//    }

    override suspend fun getIncCurrencies(context: Context?): MutableList<IncCurrency> = getIncExRate().incCurrencies

    override suspend fun getCurrencies(context: Context?): MutableList<Currency> = getExRate().currencies

    override suspend fun getAveragePercent(context: Context?): Double {
        val averagePercent = getIncExRate().averagePercent ?: -1.0

        return averagePercent
    }

    override suspend fun getMaxIncCurrency(context: Context?): IncCurrency {
        val incExRate = getIncExRate()
        return incExRate.incCurrencies[incExRate.indexMax]
    }

    override suspend fun getMinIncCurrency(context: Context?): IncCurrency {
        val incExRate = getIncExRate()
        return incExRate.incCurrencies[incExRate.indexMin]
    }

    override suspend fun getDate(context: Context?): String = getCurrenciesInfo().valCurs?.date ?: ""
}
