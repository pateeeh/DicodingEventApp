package com.example.ujiandicoding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.repository.EventsRepository
import kotlinx.coroutines.Dispatchers

class DetailViewModel(private val eventsRepository: EventsRepository) : ViewModel() {

    suspend fun insert(events: Events) {
        eventsRepository.insert(events)
    }

    suspend fun delete(events: Events) {
        eventsRepository.delete(events)
    }

    suspend fun isEventFavorited(eventId: Int): Boolean {
        return eventsRepository.isEventFavorited(eventId)
    }
}