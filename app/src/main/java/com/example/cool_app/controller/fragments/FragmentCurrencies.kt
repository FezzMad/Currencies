package com.example.cool_app.controller.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cool_app.*
import com.example.cool_app.controller.adapters.CurrencyAdapter
import com.example.cool_app.model.repository.InternalRepository
import kotlinx.android.synthetic.main.fragment_currencies.*
import kotlinx.coroutines.*

private const val TAG: String = "my_log"

class FragmentCurrencies : Fragment(R.layout.fragment_currencies) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        CoroutineScope(Job()).launch {
            val currencies = InternalRepository().getCurrencies(context)
            withContext(Dispatchers.Main) {
                val adapter =
                    CurrencyAdapter(currencies = currencies)
                recyclerViewCurrencies.layoutManager = LinearLayoutManager(context)
                recyclerViewCurrencies.adapter = adapter
            }
        }
//        // лист курса валют
//        var list: MutableList<String> = mutableListOf()
//
//        // загрузка сохранённого списка валют
//        val context = requireContext()
//        val listSave = loadListSave(context)
//        val dataSave = loadDate(context)
//
//        // создание адпатера для заполнения списка на экране
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)
//
//        if (listSave != null && dataSave != null) {
//            Log.d(TAG, "ExRate: has saved information")
//            list = parseSavedCurrencies(listSave)
//            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)
//            exchangeRateList.adapter = adapter
//        } else {
//            if (!isOnline(requireContext())) longToast("Check your internet connection")
//            Log.d(TAG, "ExRate: has not saved information")
//            list.also{
//                for (i in 1..15) it.add("Name value char code")
//            }
//            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)
//            exchangeRateList.adapter = adapter
//        }
//    }
//    private fun longToast(string: String) {
//        Toast.makeText(context, string, Toast.LENGTH_LONG).show()
    }
}
