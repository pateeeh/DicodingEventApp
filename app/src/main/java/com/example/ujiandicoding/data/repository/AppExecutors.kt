package com.example.ujiandicoding.data.repository

import android.os.Handler
import android.os.Looper
import androidx.annotation.VisibleForTesting
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors(
    val diskIO: Executor,
    val networkIO: Executor,
    val mainThread: Executor
) {

    constructor() : this(
        Executors.newFixedThreadPool(THREAD_COUNT),
        Executors.newFixedThreadPool(THREAD_COUNT),
        MainThreadExecutor()
    )

    companion object {
        private const val THREAD_COUNT = 3
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}