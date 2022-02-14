package com.example.cool_app

import android.app.*
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.cool_app.model.database.CurrencyDatabase
import com.example.cool_app.model.repository.ExternalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "my_log"

class CenterBankService : IntentService("CenterBankService") {

    init {
        instance = this
    }

    companion object {
        private lateinit var instance: CenterBankService
        var isRunning = false
        fun stopService() {
            isRunning = false
            instance.stopSelf()
            Log.d(TAG, "[-----SERVICE WAS STOPPED-----]")
        }

        val externalRepository = ExternalRepository()
        val database = CurrencyDatabase()
    }

    override fun onHandleIntent(p0: Intent?) {
        try {
            isRunning = true
            Log.d(TAG, "[-----SERVICE WAS STARTED-----]")

                if (isOnline(applicationContext)) {
                    Log.d(TAG, "[-----SERVICE READY FOR DOWNLOAD DATA-----]")
                        CoroutineScope(Job()).launch {

                            val date = externalRepository.getDate()
                            val averagePercent = externalRepository.getAveragePercent()
                            val maxIncCurrency = externalRepository.getMaxIncCurrency()
                            val minIncCurrency = externalRepository.getMinIncCurrency()
                            val incCurrencies = externalRepository.getIncCurrencies()
                            val currencies = externalRepository.getCurrencies()
                            Log.d(TAG,"\n[-----DATA WAS DOWNLOADED-----]")

                            database.saveDate(date, applicationContext)
                            database.saveAveragePercent(averagePercent, applicationContext)
                            database.saveIncCurrencies(incCurrencies, applicationContext)
                            database.saveCurrencies(currencies, applicationContext)
                            database.saveMaxIncCurrency(maxIncCurrency, applicationContext)
                            database.saveMinIncCurrency(minIncCurrency, applicationContext)
                            Log.d(TAG,"\n[-----DATA WAS SAVED-----]")

                        }
                    } else {
                        Toast.makeText(applicationContext, "NOT INTERNET CONNECTION", Toast.LENGTH_LONG).show()
                    }

            stopService()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "[-----SERVICE WAS DESTROYED-----]")
    }
}