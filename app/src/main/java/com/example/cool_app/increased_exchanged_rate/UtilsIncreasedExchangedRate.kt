package com.example.cool_app.increased_exchanged_rate

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

// Загрузка сохранённой даты
internal fun loadDate(context: Context): String? {
    val date = PreferenceManager.getDefaultSharedPreferences(context).getString("DATE", "")
    return if (date != null && date != "") date
    else null
}

// Загрузка сохранённого результата (средний процент роста, максимальный рост, минимальный рост)
internal fun loadResult(context: Context): String? {
    val result = PreferenceManager.getDefaultSharedPreferences(context).getString("RESULT", "")
    return if (result != null && result != "") result
    else null
}

// Загрузка сохранённых выросших в цене валют
internal fun loadListOfIncCurrencies(context: Context): String? {
    val listOfIncCurrencies = PreferenceManager.getDefaultSharedPreferences(context).getString("LISTOFINCCURRENCIES", "")
    return if (listOfIncCurrencies != null && listOfIncCurrencies != "") listOfIncCurrencies
    else null
}

// Сохранение полученных данных
internal fun saveValues(date: String, result: String, incCurrencies: String, context: Context) {
    val myEditor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
    myEditor.putString("DATE", date)
    myEditor.putString("RESULT", result)
    myEditor.putString("LISTOFINCCURRENCIES", incCurrencies)
    myEditor.apply()
}

// Про верка на наличе сохранённых данных
internal fun isHasSavedInfo(result: String?, date: String?, listOfIncCurrencies: String?): Boolean =
    result != null && date != null  && listOfIncCurrencies != null