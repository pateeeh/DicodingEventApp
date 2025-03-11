package com.example.ujiandicoding.ui.favorite

import androidx.lifecycle.*
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.repository.EventsRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(private val eventsRepository: EventsRepository) : ViewModel() {

    private val _eventList = MutableLiveData<List<Events>>()
    val eventList: LiveData<List<Events>> = _eventList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getFavoriteEvents()
    }

    private fun getFavoriteEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = eventsRepository.getFavoriteEvents()
            result.observeForever {
                _eventList.value = it
                _isLoading.value = false
            }
        }
    }
}