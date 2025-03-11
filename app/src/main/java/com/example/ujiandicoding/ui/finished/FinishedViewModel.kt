package com.example.ujiandicoding.ui.finished

import androidx.lifecycle.*
import com.example.ujiandicoding.data.repository.EventsRepository
import com.example.ujiandicoding.data.response.ListEventsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        viewModelScope.launch {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                eventsRepository.findFinishedEvents(_eventList, _isLoading)
            }
        }
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun clearSearch() {
        searchQuery.value = "" // Kosongkan query untuk menampilkan data asli
    }
}

