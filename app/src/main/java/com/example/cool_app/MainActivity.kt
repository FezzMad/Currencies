package com.example.cool_app

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import java.util.*
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.login_page.*
import java.text.SimpleDateFormat
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG: String = "my_log"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var isButtonPressed = false

        val myPreferenced: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)

        button.setOnClickListener {

            if (!isButtonPressed) {

                val date = myPreferenced.getString("DATE", "")
                val result = myPreferenced.getString("RESULT", "")
                val incCurrencies = myPreferenced.getString("CURRENCIES", "")

                if (isHasSavedInfo(result, date, incCurrencies)) {

                    val dateSaved = stringToDate(date = date!!)
                    Log.d(TAG, "Has saved information from date: ${dateSaved.date}.${dateSaved.month + 1}.${dateSaved.year + 1900}")
                    if (!isActualDate(dateSaved)) {

                        Log.d(TAG, "Date is not actual, actual date: ${SimpleDateFormat("dd.MM.yyyy").format(dateToday())}")
                        CoroutineScope(Job()).launch {
                            if (getInfo()) {
                                isButtonPressed = buttonPressed()
                            } else {
                                withContext(Dispatchers.Main) {
                                    displayOnScreen(date, result, parseSavedCurrencies(incCurrencies!!))
                                    longToast("Chek your Internet connection, date is not actual")
                                }
                            }
                        }
                    } else {
                        displayOnScreen(date, result, parseSavedCurrencies(incCurrencies!!))
                        Log.d(TAG, "Date is actual, loading a saved value")
                        isButtonPressed = buttonPressed()
                    } //*isActualDate*
                } else {
                    Log.d(TAG, "Has not saved information")
                    CoroutineScope(Job()).launch {
                        if (getInfo()) {
                            isButtonPressed = buttonPressed()
                        } else {
                            Log.d(TAG, "Has not Internet connection")
                            withContext(Dispatchers.Main) { longToast("Chek your Internet connection") }
                        }
                    }
                } //*isHasSavedInfo*
            } else {
                Log.d(TAG, "Button is already pressed")
            } //*isButtonPressed*
        } //*OnClickListener*

    } //*onCreate*


    private fun displayOnScreen(date: String?, result: String?, list: MutableList<String>) {
        dateView.text = "Date $date"
        resultView.text = result
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list)
        currenciesList.adapter = adapter
    }

    private fun saveValues(date: String?, result: String?, incCurrencies: String?) {
        val myPreferenced: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
        val myEditor: SharedPreferences.Editor = myPreferenced.edit()
        myEditor.putString("DATE", date)
        myEditor.putString("RESULT", result)
        myEditor.putString("CURRENCIES", incCurrencies)
        myEditor.apply()
    }

    private fun longToast(string: String) {
        Toast.makeText(baseContext, string, Toast.LENGTH_LONG).show()
    }

    private suspend fun getInfo(): Boolean = coroutineScope {
        if (isOnline(baseContext)) {
            Log.d(TAG, "Has Internet connection")
            Log.d(TAG, "Get info")
            val exchangeRateToDay = async { getCenterBankExchangeRate() }
            val dateAPI = async { getDateLastMonth(exchangeRateToDay.await()) }
            val exchangeRateLastMonth = async { getCenterBankExchangeRate(dateAPI.await().day, dateAPI.await().month, dateAPI.await().year) }
            val increasedCurrenciesProperties = async { getIncreasedCurrencies(exchangeRateToDay.await(), exchangeRateLastMonth.await()) }
            withContext(Dispatchers.Main) {
                val dateSaved: String?
                val result: String?
                var incCurrencies: String?
                val list: MutableList<String> = mutableListOf()
                if (increasedCurrenciesProperties.await().increasedCurrenciesList.isNotEmpty()) {
                    val maxValue = increasedCurrenciesProperties.await().increasedCurrenciesList[increasedCurrenciesProperties.await().indexMax]
                    val minValue = increasedCurrenciesProperties.await().increasedCurrenciesList[increasedCurrenciesProperties.await().indexMin]
                    dateSaved = exchangeRateToDay.await().valCurs?.date
                    result =
                                "Average percent +${increasedCurrenciesProperties.await().averagePercent}\n" +
                                "${maxValue.name} +${maxValue.percentageIncrease} (${maxValue.charCode})\n" +
                                "${minValue.name} +${minValue.percentageIncrease} (${minValue.charCode})"
                    incCurrencies = ""
                    increasedCurrenciesProperties.await().increasedCurrenciesList.forEach {
                        incCurrencies += "${it.name} +${it.percentageIncrease} (${it.charCode})\n"
                        list.add("${it.name} +${it.percentageIncrease} (${it.charCode})")
                    }
                } else {
                    dateSaved =
                        "${dateAPI.await().day}.${dateAPI.await().month}.${dateAPI.await().year}"
                    result = "Нет валют, которые бы повысились в цене"
                    incCurrencies = ""
                }
                displayOnScreen(dateSaved, result, list)
                saveValues(dateSaved, result, incCurrencies)
            }
            return@coroutineScope true
        } else {
            Log.d(TAG, "Has not Internet connection")
            return@coroutineScope false
        }
    }


} //*MainActivity*

private fun isHasSavedInfo(result: String?, date: String?, incCurrencies: String?): Boolean =
    result != "" && result != null && date != "" && date != null && incCurrencies != "" && incCurrencies != null

private fun buttonPressed(): Boolean {
    Log.d(TAG, "Button is pressed")
    return true
}