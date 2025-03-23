package com.example.ujiandicoding.ui.setting

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.ujiandicoding.MainActivity
import com.example.ujiandicoding.R
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.data.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class EventWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        const val TAG = "EventWorker"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "Dicoding Channel"

        fun startPeriodicWork(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<EventWorker>(15, TimeUnit.MINUTES)
                .setInitialDelay(5, TimeUnit.SECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }
    }

    override fun doWork(): Result {
        Log.d(TAG, "EventWorker dimulai...")

        val sharedPreferences =
            applicationContext.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)

        if (!notificationsEnabled) {
            Log.d(TAG, "Notifikasi dinonaktifkan, worker selesai tanpa aksi")
            return Result.success()
        }

        if (!isInternetAvailable(applicationContext)) {
            Log.e(TAG, "Tidak ada koneksi internet")
            showNotificationError("Tidak ada koneksi internet")
            return Result.failure()
        }

        return try {
            val event = getActiveEvent()
            if (event != null) {
                showNotification(event)
                Log.d(TAG, "Notifikasi berhasil dikirim untuk event: ${event.name}")
                Result.success()
            } else {
                Log.e(TAG, "Tidak ada event yang tersedia")
                showNotificationError("Tidak ada event yang tersedia")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Terjadi kesalahan: ${e.message}", e)
            showNotificationError("Terjadi kesalahan saat mengambil data event")
            Result.failure()
        }
    }

    private fun getActiveEvent(): ListEventsItem? {
        val apiService = ApiConfig.getApiService()
        return try {
            runBlocking {
                withContext(Dispatchers.IO) {
                    val response = apiService.getUpcomingEvents(active = 1)
                    if (response.isSuccessful) {
                        val eventList = response.body()?.listEvents
                        eventList?.firstOrNull()
                    } else {
                        Log.e(TAG, "Gagal mendapatkan event dari API, kode: ${response.code()}")
                        null
                    }
                }
            }
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Gagal terhubung ke server: ${e.message}", e)
            null
        } catch (e: IOException) {
            Log.e(TAG, "Terjadi kesalahan jaringan: ${e.message}", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Terjadi kesalahan tak terduga: ${e.message}", e)
            null
        }
    }

    private fun showNotification(event: ListEventsItem) {
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Izin notifikasi tidak diberikan")
            return
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle("Jangan Sampai Lupa!")
            .setContentText("${event.name} pada ${event.beginTime}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun showNotificationError(message: String) {
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Izin notifikasi tidak diberikan")
            return
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle("Pemberitahuan")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}