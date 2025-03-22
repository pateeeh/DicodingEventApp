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
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.ujiandicoding.MainActivity
import com.example.ujiandicoding.R
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.data.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class EventWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

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
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(TAG, "EventWorker dimulai...")
        val sharedPreferences =
            applicationContext.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)

        if (!isInternetAvailable(applicationContext)) {
            Log.e(TAG, "Tidak ada koneksi internet")
            showNotificationError("Tidak ada Koneksi")
            return@withContext Result.failure()
        }

        if (notificationsEnabled) {
            try {
                val event = getActiveEvent()
                if (event != null) {
                    showNotification(event)
                    Log.d(TAG, "Notifikasi berhasil dikirim untuk event: ${event.name}")
                    return@withContext Result.success()
                } else {
                    Log.e(TAG, "Tidak ada event yang tersedia")
                    showNotificationError("Tidak ada event yang tersedia")
                    return@withContext Result.failure()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Terjadi kesalahan: ${e.message}", e)
                showNotificationError("Terjadi kesalahan saat mengambil data event")
                return@withContext Result.failure()
            }
        } else {
            Log.d(TAG, "Notifikasi dinonaktifkan, worker selesai tanpa aksi")
            return@withContext Result.success()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification channel for event updates"
            }
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private suspend fun getActiveEvent(): ListEventsItem? = withContext(Dispatchers.IO) {
        val apiService = ApiConfig.getApiService()
        try {
            val response = apiService.getUpcomingEvents(1)
            if (response.isSuccessful) {
                val eventList = response.body()?.listEvents
                if (!eventList.isNullOrEmpty()) {
                    return@withContext eventList.first()
                } else {
                    Log.e(TAG, "Daftar event kosong")
                    return@withContext null
                }
            } else {
                Log.e(TAG, "Gagal mendapatkan event dari API, kode: ${response.code()}")
                return@withContext null
            }
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Gagal terhubung ke server: ${e.message}", e)
            return@withContext null
        } catch (e: IOException) {
            Log.e(TAG, "Terjadi kesalahan jaringan: ${e.message}", e)
            return@withContext null
        } catch (e: Exception) {
            Log.e(TAG, "Terjadi kesalahan tak terduga: ${e.message}", e)
            return@withContext null
        }
    }

    private fun showNotification(event: ListEventsItem) {
        Log.d(TAG, "Memanggil createNotificationChannel()")
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Izin notifikasi tidak diberikan, tidak dapat menampilkan notifikasi.")
            showNotificationError("Izin notifikasi tidak diberikan")
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

        Log.d(TAG, "Mengirim notifikasi untuk event: ${event.name}")

        try {
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menampilkan notifikasi: ${e.message}", e)
        }
    }

    private fun showNotificationError(message: String) {
        Log.d(TAG, "Memanggil createNotificationChannel()")
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Izin notifikasi tidak diberikan, tidak dapat menampilkan notifikasi.")
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

        Log.d(TAG, "Mengirim notifikasi error: $message")

        try {
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menampilkan notifikasi: ${e.message}", e)
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