package com.example.ujiandicoding.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.repository.EventsRepository

class DetailViewModel(private val eventsRepository: EventsRepository) :
    ViewModel() {
    fun insert(events: Events) {
        eventsRepository.insert(events)
    }

    fun delete(events: Events) {
        eventsRepository.delete(events)
    }

    fun isEventFavorited(eventId: Int): LiveData<Boolean> {
        return eventsRepository.isEventFavorited(eventId)
    }
}
