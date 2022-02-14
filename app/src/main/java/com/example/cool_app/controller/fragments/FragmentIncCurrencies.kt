package com.example.cool_app.controller.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cool_app.*
import kotlinx.android.synthetic.main.fragment_inc_currencies.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cool_app.model.entities.IncCurrency
import com.example.cool_app.controller.adapters.IncCurrencyAdapter
import com.example.cool_app.model.database.CurrencyDatabase
import com.example.cool_app.model.entities.EmptyData
import com.example.cool_app.model.repository.InternalRepository
import kotlinx.coroutines.*

private const val TAG = "my_log"

class FragmentIncCurrencies : Fragment(R.layout.fragment_inc_currencies) {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val context: Context = requireContext()

        //запуск серсвиса
        if (!CenterBankService.isRunning)
            Intent(context, CenterBankService::class.java).also {
                activity?.startService(it)
                Log.d(TAG, "[*****SERVICE WAS CALLED*****]")
            }

        val internalRepository = InternalRepository()

        var result: String

        if (CurrencyDatabase().isHasSavedData(context)) {
            CoroutineScope(Job()).launch {

                val date = internalRepository.getDate(context)
                val averagePercent = internalRepository.getAveragePercent(context)
                val maxIncCurrency = internalRepository.getMaxIncCurrency(context)
                val minIncCurrency = internalRepository.getMinIncCurrency(context)
                val incCurrencies = internalRepository.getIncCurrencies(context)
                Log.d(TAG, "[*****DATA WAS LOADED*****]")

                result = "Average percent: $averagePercent\n" +
                        "Max: ${maxIncCurrency.name} +${maxIncCurrency.percentageInc}\n" +
                        "Min: ${minIncCurrency.name} +${minIncCurrency.percentageInc}"

                withContext(Dispatchers.Main) {
                    displayOnScreen(date = date, result = result, list = incCurrencies)
                }
            }
        } else {
            displayOnScreen(date = EmptyData().date, result = EmptyData().result, list = EmptyData().incCurrencies)
        }

        //переход к списку валют
        buttonExchangeRate.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentIncCurrencies_to_fragmentCurrencies)
        }
    } //*OnCreate*

    //вывод данных на экран
    private fun displayOnScreen(date: String, result: String, list: MutableList<IncCurrency>) {
        dateView.text = "Date: $date"
        resultView.text = result
        val adapter =
            IncCurrencyAdapter(incCurrencies = list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView
        recyclerView.adapter = adapter
        Log.d(TAG, "[*****DATA WAS DISPLAYED*****]")
    }
}



