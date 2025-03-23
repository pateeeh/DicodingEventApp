package com.example.ujiandicoding.ui.setting

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ujiandicoding.databinding.FragmentSettingBinding
import com.example.ujiandicoding.receiver.AlarmReceiver
import kotlinx.coroutines.launch
import java.util.Calendar
import android.provider.Settings

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(
            requireActivity().application,
            SettingPreferences.getInstance(requireActivity().application.dataStore)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        // Set up the alarm to start the worker periodically
        setRepeatingAlarm()

        lifecycleScope.launch {
            settingsViewModel.getThemeSettings().collect { isDarkModeActive ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
                binding.switchTheme.isChecked = isDarkModeActive
            }
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.saveThemeSetting(isChecked)
        }

        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setNotificationsEnabled(requireContext(), isChecked)
        }

        lifecycleScope.launch {
            settingsViewModel.settings.collect { setting ->
                binding.switchNotification.isChecked = setting?.notificationsEnabled ?: false
            }
        }
    }

    private fun setRepeatingAlarm() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val intervalMillis: Long = 15 * 60 * 1000 // 15 minutes
        val triggerTime = Calendar.getInstance().apply {
            add(Calendar.SECOND, 5)
        }.timeInMillis

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            intervalMillis,
            pendingIntent
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}