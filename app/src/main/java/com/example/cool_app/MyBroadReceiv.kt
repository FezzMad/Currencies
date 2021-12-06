package com.example.cool_app

import android.content.Context
import android.content.Intent
import android.content.BroadcastReceiver

class MyBroadReceiv : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.startService(Intent(context, CenterBankService::class.java))
    }
}