package com.example.ujiandicoding

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.ujiandicoding.ui.setting.SettingPreferences
import com.example.ujiandicoding.ui.setting.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val pref = SettingPreferences.getInstance(applicationContext.dataStore)
        val isDarkModeActive = runBlocking { pref.getThemeSetting().first() }

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}