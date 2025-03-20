import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.ujiandicoding.data.db.EventsRoomDatabase
import com.example.ujiandicoding.data.db.Setting
import com.example.ujiandicoding.ui.setting.EventWorker
import com.example.ujiandicoding.ui.setting.SettingPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SettingViewModel(application: Application, private val pref: SettingPreferences) : AndroidViewModel(application) {

    private val settingDao = EventsRoomDatabase.getDatabase(application).settingDao()
    val settings: LiveData<Setting?> = settingDao.getSettings().asLiveData()

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

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
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

            if (enabled) {
                EventWorker.startPeriodicWork(context)
            } else {
                WorkManager.getInstance(context).cancelUniqueWork(EventWorker.TAG)
            }
        }
    }
}
