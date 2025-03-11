package com.example.ujiandicoding.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.db.EventsDao
import com.example.ujiandicoding.data.db.EventsRoomDatabase
import com.example.ujiandicoding.data.response.ListEventResponse
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.data.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventsRepository(application: Application) {
    private val mEventsDao: EventsDao
    private val apiService = ApiConfig.getApiService()

    init {
        val db = EventsRoomDatabase.getDatabase(application)
        mEventsDao = db.eventsDao()
    }

    suspend fun insert(events: Events) {
        withContext(Dispatchers.IO) {
            mEventsDao.insert(events)
        }
    }

    suspend fun delete(events: Events) {
        withContext(Dispatchers.IO) {
            mEventsDao.delete(events)
        }
    }

    fun getAllEvents(): LiveData<List<Events>> = mEventsDao.getAllEvents()

    suspend fun isEventFavorited(eventId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            mEventsDao.isEventFavorited(eventId)
        }
    }

    fun getFavoriteEvents(): LiveData<List<Events>> = mEventsDao.getFavoriteEvents()

    suspend fun findFinishedEvents(): List<ListEventsItem>? {
        return withContext(Dispatchers.IO) {
            val response = apiService.getFinishedEvents()
            if (response.isSuccessful) {
                response.body()?.listEvents
            } else {
                null
            }
        }
    }

    suspend fun findUpcomingEvents(): List<ListEventsItem>? {
        return withContext(Dispatchers.IO) {
            val response = apiService.getUpcomingEvents()
            if (response.isSuccessful) {
                response.body()?.listEvents
            } else {
                null
            }
        }
    }

    fun searchEvents(keyword: String): LiveData<List<ListEventsItem>> {
        return mEventsDao.searchEvents(keyword).switchMap { list ->
            MutableLiveData(list.map { event ->
                ListEventsItem(
                    id = event.id,
                    name = event.name ?: "",
                    description = event.description ?: "",
                    beginTime = event.beginTime ?: "",
                    endTime = event.endTime ?: "",
                    summary = event.summary ?: "",
                    ownerName = event.ownerName ?: "",
                    mediaCover = event.mediaCover ?: "",
                    imageLogo = event.imageLogo ?: "",
                    isFinished = event.isFavo
                )
            })
        }
    }
}