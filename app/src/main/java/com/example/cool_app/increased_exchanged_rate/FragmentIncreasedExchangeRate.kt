package com.example.cool_app.increased_exchanged_rate

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.cool_app.*
import com.example.cool_app.dateToday
import com.example.cool_app.isActualDate
import com.example.cool_app.stringToDate
import kotlinx.android.synthetic.main.fragment_increased_exchange_rate.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

private const val TAG: String = "my_log"

class FragmentIncreasedExchangeRate : Fragment(R.layout.fragment_increased_exchange_rate) {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // загрузка сохранённых данных
        val date = loadDate(requireContext())
        val result = loadResult(requireContext())
        val listOfIncCurrencies = loadListOfIncCurrencies(requireContext())

        // логика
        // (нужно упростить (как?) или поместить в отдельную функцию)
        if (isHasSavedInfo(result, date, listOfIncCurrencies)) {
            // есть сохранённые данные
            val dateSaved = stringToDate(date = date!!)
            Log.d(
                TAG,
                "Has saved information from date: ${dateSaved.date}.${dateSaved.month + 1}.${dateSaved.year + 1900}"
            )
            if (!isActualDate(dateSaved) && Date().day != 0) {
                // данные устарели
                Log.d(
                    TAG,
                    "Date is not actual, actual date: ${
                        SimpleDateFormat("dd.MM.yyyy").format(dateToday())
                    }"
                )
                if (isOnline(requireContext())) {
                    // есть интернет-соединение
                    CoroutineScope(Job()).launch {
                        Log.d(TAG, "Has Internet connection")
                        val resultObj = getInfo()
                        Log.d(TAG, "Get info")
                        saveValues(
                            resultObj.date!!,
                            resultObj.result,
                            resultObj.incCurrencies,
                            requireContext()
                        )
                        withContext(Dispatchers.Main) {
                            displayOnScreen(
                                resultObj.date,
                                resultObj.result,
                                resultObj.list
                            )
                        }
                    }
                } else {
                    // нет интернет соединения
                    displayOnScreen(date, result, parseSavedCurrencies(listOfIncCurrencies!!))
                    longToast("Chek your Internet connection, date is not actual")
                }
            } else {
                displayOnScreen(date, result, parseSavedCurrencies(listOfIncCurrencies!!))
                Log.d(TAG, "Date is actual, loading a saved value")
            } //*isActualDate*
        } else {
            // сохранённых данных нет
            Log.d(TAG, "Has not saved information")
            if (isOnline(requireContext())) {
                // есть интернет-соединение
                CoroutineScope(Job()).launch {
                    Log.d(TAG, "Has Internet connection")
                    val resultObj = getInfo()
                    Log.d(TAG, "Get info")
                    saveValues(
                        resultObj.date!!,
                        resultObj.result,
                        resultObj.incCurrencies,
                        requireContext()
                    )
                    withContext(Dispatchers.Main) {
                        displayOnScreen(
                            resultObj.date,
                            resultObj.result,
                            resultObj.list
                        )
                    }
                }
            } else {
                // нет интернет-соединения
                Log.d(TAG, "Has not Internet connection")
                longToast("Chek your Internet connection")
            }
        } //*isHasSavedInfo*

        // переход на страницу с курсом валют
        buttonExchangeRate.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentIncreasedExchangeRate_to_fragmentExchangeRate)
        }

    } //*OnCreate*

    // Вывод данных на экран
    private fun displayOnScreen(date: String?, result: String?, list: MutableList<String>) {
        dateView.text = "Date $date"
        resultView.text = result
        val adapter =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, list)
        currenciesList.adapter = adapter
    }

    // Оповещение пользователя
    private fun longToast(string: String) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).show()
    }

}//*Class*



