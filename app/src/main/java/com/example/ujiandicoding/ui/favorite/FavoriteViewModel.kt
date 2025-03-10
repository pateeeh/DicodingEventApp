package com.example.ujiandicoding.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.repository.EventsRepository

class FavoriteViewModel(private val eventsRepository: EventsRepository) : ViewModel() {

    private val _eventList = MutableLiveData<List<Events>>()
    val eventList: LiveData<List<Events>> = _eventList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getFavoriteEvents()
    }

    private fun getFavoriteEvents() {
        _isLoading.value = true
        val list = eventsRepository.getFavoriteEvents()
        list.observeForever {
            _eventList.value = it
            _isLoading.value = false
        }
    }
}