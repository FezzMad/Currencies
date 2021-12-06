package com.example.cool_app

import android.app.*
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.cool_app.exchange_rate.saveExRateValues
import com.example.cool_app.increased_exchanged_rate.saveIncExRateValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import android.os.Build

private const val TAG = "my_log"

class CenterBankService : IntentService("CenterBankService") {

    init {
        instance = this
    }

    companion object {
        private lateinit var instance: CenterBankService
        var isRunning = false
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channelID1"

        fun stopService() {
            Log.d(TAG, "CenterBankService is stopping...")
            isRunning = false
            instance.stopSelf()
        }
    }

    override fun onHandleIntent(p0: Intent?) {
        try {
            isRunning = true

            //период обновления 5 секунд
            var period = 5L

            while (isRunning) {
                Log.d(TAG, "CenterBankService is running...")
                try {
                    if (isOnline(applicationContext)) {
                        CoroutineScope(Job()).launch {
                            Log.d(TAG, "Service: has Internet connection")
                            val incExRate = getIncExRate()
                            val exRate = getExRate()
                            Log.d(TAG, "Service: information is received")
                            saveIncExRateValues(
                                incExRate.date!!,
                                incExRate.result,
                                incExRate.incCurrencies,
                                applicationContext
                            )
                            saveExRateValues(
                                exRate.listSave,
                                applicationContext
                            )
                            Log.d(TAG, "Service: information was saved")
                            if (incExRate.numbOfMoreTwoPerc != 0) notify(incExRate.numbOfMoreTwoPerc)

                            //период обновления 1 день
                            period = 86400
                        }
                    } else {
                        Log.d(TAG, "Service: has not Internet connection")
                    }
                    TimeUnit.SECONDS.sleep(period)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service was destroyed")
    }

    //создание notification
    fun notify(number: Int) {
        val ies = if (number > 1) "ies" else "y"

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.label)
            .setContentTitle("A major leap!")
            .setContentText("The $number currenc$ies rose by more than two percent")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Leap"
            val descriptionText = "Notification about a major leap"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(NOTIFICATION_ID, builder.build())
        }

        Log.d(TAG,"Srvice: notification")
    }
}