package com.example.cool_app.model.database

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.example.cool_app.model.entities.Currency
import com.example.cool_app.model.entities.IncCurrency

class CurrencyDatabase {

    companion object {
        private const val TAG: String = "my_log"

        const val dateKey: String = "DATE"
        const val averagePercentKey: String = "AVERAGE_PERCENT"
        const val incCurrenciesKey: String = "INC_CURRENCIES"
        const val currenciesKey: String = "CURRENCIES"
        const val minIncCurrencyKey: String = "MIN_INC_CURRENCY"
        const val maxIncCurrencyKey: String = "MAX_INC_CURRENCY"
        const val empty: String = ""
    }

    /**LOAD**/
    internal fun loadDate(context: Context): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(dateKey, empty)
    }

    fun loadIncCurrencies(context: Context): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(incCurrenciesKey, empty)
    }

    internal fun loadCurrencies(context: Context): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(currenciesKey, empty)
    }

    internal fun loadAveragePercent(context: Context): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(averagePercentKey, empty)
    }

    internal fun loadMinIncCurrency(context: Context): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(minIncCurrencyKey, empty)
    }

    internal fun loadMaxIncCurrency(context: Context): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(maxIncCurrencyKey, empty)
    }

    /**SAVE**/
    fun saveDate(date: String, context: Context) {
        val myEditor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        myEditor.putString(dateKey, date)
        myEditor.apply()
        Log.d(TAG," \n[#####SAVED INFORMATION#####]\n$date")
    }

    fun saveIncCurrencies(incCurrencies: MutableList<IncCurrency>, context: Context) {
        var incCurrenciesStr = ""
        for (incCurrency in incCurrencies) {
            incCurrenciesStr += "${incCurrency.name} +${incCurrency.percentageInc} [${incCurrency.charCode}]\n"
        }
        val myEditor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        myEditor.putString(incCurrenciesKey, incCurrenciesStr)
        myEditor.apply()
        Log.d(TAG," \n[#####SAVED INFORMATION#####]\n$incCurrenciesStr")
    }

    fun saveCurrencies(currencies: MutableList<Currency>, context: Context) {
        var currenciesStr = ""
        for (currency in currencies) {
            currenciesStr += "${currency.name} +${currency.value} [${currency.charCode}]\n"
        }
        val myEditor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        myEditor.putString(currenciesKey, currenciesStr)
        myEditor.apply()
        Log.d(TAG," \n[#####SAVED INFORMATION#####]\n$currenciesStr")
    }

    fun saveAveragePercent(averagePercent: Double, context: Context) {
        val myEditor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        myEditor.putString(averagePercentKey, averagePercent.toString())
        myEditor.apply()
        Log.d(TAG," \n[#####SAVED INFORMATION#####]\n$averagePercent")
    }

    fun saveMaxIncCurrency(maxIncCurrency: IncCurrency, context: Context) {
        val maxIncCurrencyStr = "${maxIncCurrency.name} +${maxIncCurrency.percentageInc} [${maxIncCurrency.charCode}]"
        val myEditor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        myEditor.putString(maxIncCurrencyKey, maxIncCurrencyStr)
        myEditor.apply()
        Log.d(TAG," \n[#####SAVED INFORMATION#####]\n$maxIncCurrencyStr")
    }

    fun saveMinIncCurrency(minIncCurrency: IncCurrency, context: Context) {
        val minIncCurrencyStr = "${minIncCurrency.name} +${minIncCurrency.percentageInc} [${minIncCurrency.charCode}])"
        val myEditor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        myEditor.putString(minIncCurrencyKey, minIncCurrencyStr)
        myEditor.apply()
        Log.d(TAG," \n[#####SAVED INFORMATION#####]\n$minIncCurrencyStr")
    }

    fun isHasSavedData(context: Context): Boolean {
        val isHasDate = !loadDate(context).isNullOrEmpty()
        val isHasIncCurrencies = !loadIncCurrencies(context).isNullOrEmpty()
        val isHasCurrencies = !loadCurrencies(context).isNullOrEmpty()
        val isHasAveragePercent = !loadAveragePercent(context).isNullOrEmpty()
        val isHasMinIncCurrency = !loadMinIncCurrency(context).isNullOrEmpty()
        val isHasMaxIncCurrency = !loadMaxIncCurrency(context).isNullOrEmpty()
        return isHasDate && isHasIncCurrencies && isHasCurrencies && isHasAveragePercent && isHasMinIncCurrency && isHasMaxIncCurrency
    }

}