package com.example.ujiandicoding.ui.finished

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ujiandicoding.data.repository.EventsRepository

class FinishedViewModelFactory(private val eventsRepository: EventsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinishedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinishedViewModel(eventsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}