package com.example.ujiandicoding.ui.upcoming

import androidx.lifecycle.*
import com.example.ujiandicoding.data.repository.EventsRepository
import com.example.ujiandicoding.data.response.ListEventsItem
import kotlinx.coroutines.launch

class UpcomingViewModel(private val eventsRepository: EventsRepository) : ViewModel() {

    private val _eventList = MutableLiveData<List<ListEventsItem>>()
    val eventList: LiveData<List<ListEventsItem>> = _eventList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        findUpcomingEvents()
    }

    private fun findUpcomingEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            val events = eventsRepository.findUpcomingEvents()
            _eventList.postValue(events ?: emptyList())
            _isLoading.postValue(false)
        }
    }
}