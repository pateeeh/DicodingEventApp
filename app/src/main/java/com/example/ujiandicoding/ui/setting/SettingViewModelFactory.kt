import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ujiandicoding.ui.setting.SettingPreferences

class SettingViewModelFactory(
    private val application: Application,
    private val pref: SettingPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(application, pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}