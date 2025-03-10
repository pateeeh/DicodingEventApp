package com.example.ujiandicoding.ui.upcoming

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.repository.EventsRepository
import com.example.ujiandicoding.data.response.ListEventResponse
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingViewModel(private val eventsRepository: EventsRepository) : ViewModel() {

    private val _eventList = MutableLiveData<List<ListEventsItem>>()
    val eventList: LiveData<List<ListEventsItem>> = _eventList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        findUpcomingEvents()
    }

    private fun findUpcomingEvents() {
        eventsRepository.findUpcomingEvents(_eventList, _isLoading)
    }
}
//    fun getUpcomingEvents() = eventsRepository.getUpcomingEvents()
//    fun getFavoriteEvents() = eventsRepository.getFavoriteEvents()
//    fun saveFavoriteEvents(events: Events) {
//        viewModelScope.launch {
//            eventsRepository.setFavoriteEvents(events, true)
//        }
//    }
//    fun deleteFavoriteEvents(events: Events) {
//        viewModelScope.launch {
//            eventsRepository.setFavoriteEvents(events, false)
//        }
//    }
