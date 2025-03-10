package com.example.ujiandicoding.ui.finished

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.repository.EventsRepository
import com.example.ujiandicoding.data.response.ListEventResponse
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishedViewModel(private val eventsRepository: EventsRepository): ViewModel() {
    private val _eventList = MutableLiveData<List<ListEventsItem>>()
    val eventList: LiveData<List<ListEventsItem>> = _eventList
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        findFinishedEvents()
    }

    private fun findFinishedEvents() {
        eventsRepository.findFinishedEvents(_eventList, _isLoading)
    }
}