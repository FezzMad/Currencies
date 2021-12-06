package com.example.cool_app.increased_exchanged_rate

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.cool_app.*
import kotlinx.android.synthetic.main.fragment_increased_exchange_rate.*
import android.content.Intent

private const val TAG: String = "my_log"

class FragmentIncreasedExchangeRate : Fragment(R.layout.fragment_increased_exchange_rate) {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //запуск серсвиса, если не запущен
        if (!CenterBankService.isRunning)
            Intent(context, CenterBankService::class.java).also {
                activity?.startService(it)
            }

        //переход к списку валют
        buttonExchangeRate.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentIncreasedExchangeRate_to_fragmentExchangeRate)
        }

        //переход к настройкам
        buttonSettings.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentIncreasedExchangeRate_to_fragmentSettings)
        }

        // загрузка данных и вывод на экран
        var date = loadDate(requireContext())
        var result = loadResult(requireContext())
        val listOfIncCurrencies = loadListOfIncCurrencies(requireContext())
        if (result != null && date != null && listOfIncCurrencies != null) {
            Log.d(TAG, "IncExRate: has saved information")
            displayOnScreen(date, result, parseSavedCurrencies(listOfIncCurrencies))
        } else {
            if (!isOnline(requireContext())) longToast("Check your internet connection")
            Log.d(TAG, "IncExRate: has not saved information")
            //формирование пустых полей
            date = ""
            result = "Result information"
            val emptyList = mutableListOf("")
            emptyList.also {
                for (i in 1..15) it.add("Name value (char code)")
            }
            displayOnScreen(date, result, emptyList)
            longToast("Load")
        }
    } //*OnCreate*

    //вывод данных на экран
    private fun displayOnScreen(date: String?, result: String?, list: MutableList<String>) {
        dateView.text = "Date $date"
        resultView.text = result
        val adapter =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, list)
        currenciesList.adapter = adapter
    }

    //оповещение пользователя
    private fun longToast(string: String) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).show()
    }
}



