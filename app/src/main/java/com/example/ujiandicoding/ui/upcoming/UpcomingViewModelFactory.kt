package com.example.ujiandicoding.ui.upcoming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ujiandicoding.data.repository.EventsRepository

class UpcomingViewModelFactory(private val eventsRepository: EventsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpcomingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UpcomingViewModel(eventsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}