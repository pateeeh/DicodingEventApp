package com.example.ujiandicoding.ui.finished

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.repository.EventsRepository
import com.example.ujiandicoding.data.response.ListEventResponse
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FinishedViewModel(private val eventsRepository: EventsRepository) : ViewModel() {
    private val _eventList = MutableLiveData<List<ListEventsItem>>()
    val eventList: LiveData<List<ListEventsItem>> = _eventList
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData untuk pencarian
    private val searchQuery = MutableLiveData<String>()

    // Transform hasil pencarian agar tidak merusak data asli
    val searchedEvents: LiveData<List<ListEventsItem>> = MediatorLiveData<List<ListEventsItem>>().apply {
        addSource(_eventList) { value = filterEvents(it, searchQuery.value) }
        addSource(searchQuery) { value = filterEvents(_eventList.value, it) }
    }

    private fun filterEvents(events: List<ListEventsItem>?, query: String?): List<ListEventsItem> {
        return if (query.isNullOrEmpty()) events ?: emptyList()
        else events?.filter { it.name.contains(query, ignoreCase = true) } ?: emptyList()
    }

    init {
        findFinishedEvents()
    }

    fun findFinishedEvents() {
        eventsRepository.findFinishedEvents(_eventList, _isLoading)
        Log.d("FinishedViewModel", "Fetching finished events...")
        _eventList.observeForever {
            Log.d("FinishedViewModel", "Fetched events: $it")
        }
    }

    fun searchEvents(keyword: String) {
        searchQuery.value = keyword
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun clearSearch() {
        searchQuery.value = "" // Kosongkan query untuk menampilkan data asli
    }
}