package com.example.cool_app.exchange_rate

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

internal fun saveExRateValues(listSave: String, context: Context) {
    val myEditor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
    myEditor.putString("LISTSAVE", listSave)
    myEditor.apply()
}

internal fun loadListSave(context: Context): String? {
    val result = PreferenceManager.getDefaultSharedPreferences(context).getString("LISTSAVE", "")
    return if (result != null && result != "") result
    else null
}
