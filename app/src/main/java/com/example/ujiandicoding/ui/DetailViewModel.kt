package com.example.ujiandicoding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.repository.EventsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailViewModel(private val eventsRepository: EventsRepository) : ViewModel() {

    suspend fun insert(events: Events) {
        withContext(Dispatchers.IO) {
            eventsRepository.insert(events)
        }
    }

    suspend fun delete(events: Events) {
        withContext(Dispatchers.IO) {
            eventsRepository.delete(events)
        }
    }

    suspend fun isEventFavorited(eventId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            eventsRepository.isEventFavorited(eventId)
        }
    }
}

