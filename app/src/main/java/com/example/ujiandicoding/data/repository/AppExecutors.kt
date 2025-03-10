package com.example.ujiandicoding.data.repository

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AppExecutors(
    val diskIO: ExecutorService = Executors.newSingleThreadExecutor()
) {
    fun diskIO(): ExecutorService = diskIO
}