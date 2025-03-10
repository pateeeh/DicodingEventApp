package com.example.ujiandicoding.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.db.EventsDao
import com.example.ujiandicoding.data.db.EventsRoomDatabase
import com.example.ujiandicoding.data.response.ListEventResponse
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EventsRepository(application: Application, private val appExecutors: AppExecutors) {
    private val mEventsDao: EventsDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val apiService = ApiConfig.getApiService()

    init {
        val db = EventsRoomDatabase.getDatabase(application)
        mEventsDao = db.eventsDao()
    }

    fun insert(events: Events) {
        appExecutors.diskIO.execute { mEventsDao.insert(events) }
    }

    fun delete(events: Events) {
        appExecutors.diskIO.execute { mEventsDao.delete(events) }
    }

    fun getAllEvents(): LiveData<List<Events>> = mEventsDao.getAllEvents()

    fun isEventFavorited(eventId: Int): LiveData<Boolean> = mEventsDao.isEventFavorited(eventId)

    fun getFavoriteEvents(): LiveData<List<Events>> = mEventsDao.getFavoriteEvents()

    fun findFinishedEvents(eventList: MutableLiveData<List<ListEventsItem>>, isLoading: MutableLiveData<Boolean>) {
        isLoading.value = true
        val client = apiService.getFinishedEvents()
        client.enqueue(object : Callback<ListEventResponse> {
            override fun onResponse(
                call: Call<ListEventResponse>,
                response: Response<ListEventResponse>
            ) {
                isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        eventList.value = responseBody.listEvents
                    }
                } else {
                    Log.e("EventsRepository", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ListEventResponse>, t: Throwable) {
                isLoading.value = false
                Log.e("EventsRepository", "onFailure: ${t.message}")
            }
        })
    }

    fun findUpcomingEvents(eventList: MutableLiveData<List<ListEventsItem>>, isLoading: MutableLiveData<Boolean>) {
        isLoading.value = true
        val client = apiService.getUpcomingEvents()
        client.enqueue(object : Callback<ListEventResponse> {
            override fun onResponse(
                call: Call<ListEventResponse>,
                response: Response<ListEventResponse>
            ) {
                isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        eventList.value = responseBody.listEvents
                    }
                } else {
                    Log.e("EventsRepository", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ListEventResponse>, t: Throwable) {
                isLoading.value = false
                Log.e("EventsRepository", "onFailure: ${t.message}")
            }
        })
    }
}

