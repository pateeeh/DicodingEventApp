package com.example.ujiandicoding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ujiandicoding.data.repository.EventsRepository

class DetailViewModelFactory(private val eventsRepository: EventsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(eventsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}