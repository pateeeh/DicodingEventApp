package com.example.ujiandicoding.ui.setting

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.ujiandicoding.MainActivity
import com.example.ujiandicoding.R
import com.example.ujiandicoding.data.db.Events
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class EventWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        const val TAG = "EventWorker"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "dicoding channel"

        fun startPeriodicWork(context: Context) {
            val workRequest = PeriodicWorkRequest.Builder(EventWorker::class.java, 1, TimeUnit.DAYS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }
    }

    override fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", false)

        if (notificationsEnabled) {
            val event = getActiveEvent()
            event?.let { showNotification(it) }
        }

        return Result.success()
    }

    private fun getActiveEvent(): Events? {
        val url = URL("https://event-api.dicoding.dev/events?active=-1&limit=1")
        val connection = url.openConnection() as HttpURLConnection
        Log.d(TAG,"getEvents: $url")

        return try {
            connection.requestMethod = "GET"
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseBody)
                val eventArray = json.getJSONArray("events")
                if (eventArray.length() > 0) {
                    val eventJson = eventArray.getJSONObject(0)
                    Events(
                        id = eventJson.getInt("id"),
                        name = eventJson.getString("name"),
                        description = eventJson.optString("description"),
                        image = eventJson.optString("image"),
                        beginTime = eventJson.optString("beginTime"),
                        endTime = eventJson.optString("endTime"),
                        isFavo = eventJson.optBoolean("isFavo"),
                        summary = eventJson.optString("summary"),
                        ownerName = eventJson.optString("ownerName"),
                        mediaCover = eventJson.optString("mediaCover"),
                        imageLogo = eventJson.optString("imageLogo")
                    )
                } else null
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection.disconnect()
        }
    }

    private fun showNotification(event: Events) {
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle("Jangan Sampai Lupa")
            .setContentText("${event.name} pada ${event.beginTime}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = "Notification channel for event updates"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}