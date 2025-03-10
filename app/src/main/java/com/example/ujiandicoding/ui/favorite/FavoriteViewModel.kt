package com.example.ujiandicoding.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.repository.EventsRepository
import com.example.ujiandicoding.data.response.ListEventsItem

class FavoriteViewModel(private val eventsRepository: EventsRepository) : ViewModel() {
    private val _eventList = MutableLiveData<List<ListEventsItem>>()
    val eventList: LiveData<List<ListEventsItem>> = _eventList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getFavoriteEvents()
    }

    private fun getFavoriteEvents() {
        eventsRepository.findUpcomingEvents(_eventList, _isLoading)
    }
}