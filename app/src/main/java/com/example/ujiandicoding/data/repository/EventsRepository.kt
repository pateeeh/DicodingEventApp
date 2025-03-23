package com.example.ujiandicoding.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.db.EventsDao
import com.example.ujiandicoding.data.db.EventsRoomDatabase
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

}