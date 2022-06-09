package com.example.pillreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

class broadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals("com.tester.alarmmanager")){
            var b = intent?.extras
            Toast.makeText(context,"${b?.getString("message")} innnn",Toast.LENGTH_LONG).show()

        }
    }
}