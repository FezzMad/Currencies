package com.example.cool_app.model.repository

import android.content.Context
import com.example.cool_app.model.entities.Currency
import com.example.cool_app.model.entities.IncCurrency

interface CurrenciesRepository {

    suspend fun getIncCurrencies(context: Context? = null): MutableList<IncCurrency>

    suspend fun getCurrencies(context: Context? = null): MutableList<Currency>

    suspend fun getAveragePercent(context: Context? = null): Double

    suspend fun getMaxIncCurrency(context: Context? = null): IncCurrency

    suspend fun getMinIncCurrency(context: Context? = null): IncCurrency

    suspend fun getDate(context: Context? = null): String

}