package com.example.ujiandicoding.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.ujiandicoding.ui.setting.EventWorker

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm diterima, menjalankan EventWorker...")
        val workRequest = OneTimeWorkRequest.Builder(EventWorker::class.java).build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

}