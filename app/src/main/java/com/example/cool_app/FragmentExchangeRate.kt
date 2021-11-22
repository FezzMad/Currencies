package com.example.cool_app

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_exchange_rate.*

import kotlinx.coroutines.*

private const val TAG: String = "my_log"

class FragmentExchangeRate : Fragment(R.layout.fragment_exchange_rate) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // лист курса валют
        var list: MutableList<String> = mutableListOf()

        // для сохранения/загрузки списка валют
        val myPreferenced: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
        val myEditor: SharedPreferences.Editor = myPreferenced.edit()

        // загрузка сохранённого списка валют
        var listSave = myPreferenced.getString("LISTSAVE", "")

        // создание адпатера для заполнения списка на экране
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)

        // проверка на наличе сохранённого списка
        // (нужно добавить проверку на актуальность данных,
        // сейчас информация загружается только один раз)
        if (listSave != "" && listSave != null) {
            // сохранённый список есть
            Log.d(TAG, "Has saved information")
            list = parseSavedCurrencies(listSave)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)
            // вывод списка на экран
            exchangeRateList.adapter = adapter
        } else {
            // сохранённого списока нет
            Log.d(TAG, "Has not saved information")
            CoroutineScope(Job()).launch {
                if (isOnline(requireContext())) {
                    Log.d(TAG, "Has internet connection")
                    getCenterBankExchangeRate().valCurs?.currency?.forEach { list.add("${it.name} ${it.value} ${it.charCode}") }
                    list.forEach { listSave += "$it\n" }
                    myEditor.putString("LISTSAVE", listSave)
                    myEditor.apply()
                    withContext(Dispatchers.Main) { exchangeRateList.adapter = adapter }
                } else {
                    Log.d(TAG, "Has not internet connection")
                    withContext(Dispatchers.Main) {Toast.makeText(
                        requireContext(),
                        "Chek your Internet connection",
                        Toast.LENGTH_LONG
                    ).show() }
                }
            }
        }
    }
}

