package com.example.ujiandicoding.ui.setting

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.ujiandicoding.data.db.EventsRoomDatabase
import com.example.ujiandicoding.data.db.Setting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SettingViewModel(application: Application, private val pref: SettingPreferences) : AndroidViewModel(application) {

    private val settingDao = EventsRoomDatabase.getDatabase(application).settingDao()
    val settings: Flow<Setting?> = settingDao.getSettings()

    init {
        initializeSetting()
    }

    private fun initializeSetting() {
        viewModelScope.launch {
            val existingSetting = settingDao.getSettings().firstOrNull()
            if (existingSetting == null) {
                settingDao.insertSetting(Setting(id = 0, notificationsEnabled = false))
            }
        }
    }

    fun getThemeSettings(): Flow<Boolean> {
        return pref.getThemeSetting()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            val currentSetting = settingDao.getSettings().firstOrNull() ?: Setting()
            val newSetting = currentSetting.copy(notificationsEnabled = enabled)
            settingDao.updateSetting(newSetting)

            val workManager = WorkManager.getInstance(context)

            if (enabled) {
                Log.d("com.example.ujiandicoding.ui.setting.SettingViewModel", "Starting EventWorker")
                EventWorker.startPeriodicWork(context)
            } else {
                Log.d("com.example.ujiandicoding.ui.setting.SettingViewModel", "Stopping EventWorker")
                workManager.cancelUniqueWork(EventWorker.TAG)
            }
        }
    }
}