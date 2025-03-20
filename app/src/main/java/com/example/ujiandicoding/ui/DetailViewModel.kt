package com.example.ujiandicoding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.repository.EventsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel(private val eventsRepository: EventsRepository) : ViewModel() {

    suspend fun isEventFavorited(eventId: Int): Boolean {
        return eventsRepository.isEventFavorited(eventId)
    }

    fun insert(event: Events) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.insert(event)
        }
    }

    fun delete(event: Events) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.delete(event)
        }
    }
}