package com.example.ujiandicoding

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ujiandicoding.databinding.ActivityMainBinding
import com.example.ujiandicoding.ui.setting.SettingPreferences
import com.example.ujiandicoding.ui.setting.SettingViewModel
import com.example.ujiandicoding.ui.setting.SettingViewModelFactory
import com.example.ujiandicoding.ui.setting.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)


        if (supportActionBar != null) {
            setupActionBarWithNavController(navController)
        }
        navView.setupWithNavController(navController)
    }

}