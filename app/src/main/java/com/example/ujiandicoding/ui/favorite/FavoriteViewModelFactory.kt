package com.example.ujiandicoding.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ujiandicoding.data.repository.EventsRepository

class FavoriteViewModelFactory(private val eventsRepository: EventsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(eventsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}