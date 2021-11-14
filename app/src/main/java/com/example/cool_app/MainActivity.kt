package com.example.cool_app

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import java.util.*
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.login_page.*
import java.text.SimpleDateFormat

private const val TAG: String = "my_log"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shortToast("onCreate")

        val textView = findViewById<TextView>(R.id.textView)
        val button = findViewById<Button>(R.id.button)
        var isButtonPressed = false

        val myPreferenced: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
        val myEditor: SharedPreferences.Editor = myPreferenced.edit()


        button.setOnClickListener {

            if (!isButtonPressed) {

                var result = myPreferenced.getString("RESULT", "")
                if (result != "" && result != null) {

                    result += "\n It is saved value!"
                    val date = stringToDate(date = result.substring(6, 16))
                    Log.d(TAG, "Has saved information from date: ${date.date}.${date.month + 1}.${date.year+1900}")
                    if (!isActualDate(date)) {

                        Log.d(TAG, "Date is not actual, actual date: ${SimpleDateFormat("dd.MM.yyyy").format(dateToday())}")
                        if (isOnline(this)) {

                            Log.d(TAG, "Has Internet connection")
                            CoroutineScope(Job()).launch {

                                Log.d(TAG, "Get API information")
                                val exchangeRateToDay = async { getCenterBankExchangeRate() }
                                val dateAPI = async { getDateLastMonth(exchangeRateToDay.await()) }
                                val exchangeRateLastMonth = async { getCenterBankExchangeRate(dateAPI.await().day, dateAPI.await().month, dateAPI.await().year) }
                                val increasedCurrenciesProperties = async { getIncreasedCurrencies(exchangeRateToDay.await(), exchangeRateLastMonth.await()) }
                                withContext(Dispatchers.Main) {
                                    if (increasedCurrenciesProperties.await().increasedCurrenciesList.isNotEmpty()) {
                                        val maxValue = increasedCurrenciesProperties.await().increasedCurrenciesList[increasedCurrenciesProperties.await().indexMax]
                                        val minValue = increasedCurrenciesProperties.await().increasedCurrenciesList[increasedCurrenciesProperties.await().indexMin]
                                        result =
                                                    "Date: ${exchangeRateToDay.await().valCurs?.date}\n" +
                                                            "\n" +
                                                    "Average percent = +${increasedCurrenciesProperties.await().averagePercent}\n" +
                                                    "Max: ${maxValue.name} +${maxValue.percentageIncrease} (${maxValue.charCode})\n" +
                                                    "Min: ${minValue.name} +${minValue.percentageIncrease} (${minValue.charCode})\n" +
                                                    "\n"

                                        increasedCurrenciesProperties.await().increasedCurrenciesList.forEach {
                                            result += "${it.name} +${it.percentageIncrease} (${it.charCode})\n"
                                        }
                                    } else result = "Нет валют, которые бы повысились в цене"
                                    textView.text = result
                                    myEditor.putString("RESULT", result)
                                    myEditor.apply()
                                    isButtonPressed = true
                                    Log.d(TAG, "Button is pressed")
                                }
                            } //*CoroutineScope(Job()).launch*
                        } else {
                            Log.d(TAG, "Has not Internet connection")
                            shortToast("Chek your Internet connection")
                        } //*isOnline*
                    } else {
                        textView.text = result
                        Log.d(TAG, "Date is actual, loading a saved value")

                        isButtonPressed = true
                        Log.d(TAG, "Button is pressed")
                    } //*isActualDate*
                } else {
                    Log.d(TAG, "Do not has saved value")
                    // срабртает при первом запуске приложения
                    // запрос к API, нужно придумать, как его запихнуть функцию
                } //*result*
            } else {
                Log.d(TAG, "Button is already pressed")
            } //*isButtonPressed*
        } //*OnClickListener*
    } //*onCreate*

    override fun onStart() {
        super.onStart()
        shortToast("onStart")
    }

    override fun onResume() {
        super.onResume()
        shortToast("onResume")
    }

    override fun onPause() {
        super.onPause()
        shortToast("onPause")
    }

    override fun onStop() {
        super.onStop()
        shortToast("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        shortToast("onDestroy")
    }

    private fun shortToast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

} //*MainActivity*