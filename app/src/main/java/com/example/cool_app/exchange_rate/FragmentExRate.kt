package com.example.cool_app.exchange_rate

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cool_app.*
import com.example.cool_app.increased_exchanged_rate.loadDate
import kotlinx.android.synthetic.main.fragment_exchange_rate.*

private const val TAG: String = "my_log"

class FragmentExchangeRate : Fragment(R.layout.fragment_exchange_rate) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // лист курса валют
        var list: MutableList<String> = mutableListOf()

        // загрузка сохранённого списка валют
        val context = requireContext()
        val listSave = loadListSave(context)
        val dataSave = loadDate(context)

        // создание адпатера для заполнения списка на экране
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)

        if (listSave != null && dataSave != null) {
            Log.d(TAG, "ExRate: has saved information")
            list = parseSavedCurrencies(listSave)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)
            exchangeRateList.adapter = adapter
        } else {
            if (!isOnline(requireContext())) longToast("Check your internet connection")
            Log.d(TAG, "ExRate: has not saved information")
            list.also{
                for (i in 1..15) it.add("Name value char code")
            }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)
            exchangeRateList.adapter = adapter
        }
    }
    private fun longToast(string: String) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).show()
    }
}
