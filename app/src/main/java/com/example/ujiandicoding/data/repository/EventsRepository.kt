package com.example.ujiandicoding.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.example.ujiandicoding.data.db.Events
import com.example.ujiandicoding.data.db.EventsDao
import com.example.ujiandicoding.data.db.EventsRoomDatabase
import com.example.ujiandicoding.data.response.ListEventResponse
import com.example.ujiandicoding.data.response.ListEventsItem
import com.example.ujiandicoding.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        isLoading.postValue(true)
        val client = apiService.getFinishedEvents()
        client.enqueue(object : Callback<ListEventResponse> {
            override fun onResponse(
                call: Call<ListEventResponse>,
                response: Response<ListEventResponse>
            ) {
                isLoading.postValue(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val result = responseBody.listEvents?.map {
                            Events(
                                id = it.id,
                                name = it.name,
                                description = it.description,
                                image = it.imageLogo,
                                beginTime = it.beginTime,
                                endTime = it.endTime,
                                summary = it.summary,
                                ownerName = it.ownerName,
                                mediaCover = it.mediaCover,
                                imageLogo = it.imageLogo,
                                isFavo = true
                            )
                        }
                        if (result != null) {
                            for (event in result) {
                                insert(event)
                            }
                        }
                        eventList.postValue(responseBody.listEvents)
                    }
                } else {
                    Log.e("EventsRepository", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ListEventResponse>, t: Throwable) {
                isLoading.postValue(false)
                Log.e("EventsRepository", "onFailure: ${t.message}")
            }
        })
    }

    fun findUpcomingEvents(eventList: MutableLiveData<List<ListEventsItem>>, isLoading: MutableLiveData<Boolean>) {
        isLoading.postValue(true)
        val client = apiService.getUpcomingEvents()
        client.enqueue(object : Callback<ListEventResponse> {
            override fun onResponse(
                call: Call<ListEventResponse>,
                response: Response<ListEventResponse>
            ) {
                isLoading.postValue(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val result = responseBody.listEvents?.map {
                            Events(
                                id = it.id,
                                name = it.name,
                                description = it.description,
                                image = it.imageLogo,
                                beginTime = it.beginTime,
                                endTime = it.endTime,
                                summary = it.summary,
                                ownerName = it.ownerName,
                                mediaCover = it.mediaCover,
                                imageLogo = it.imageLogo,
                                isFavo = false
                            )
                        }
                        if (result != null) {
                            for (event in result) {
                                insert(event)
                            }
                        }
                        eventList.postValue(responseBody.listEvents)
                    }
                } else {
                    Log.e("EventsRepository", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ListEventResponse>, t: Throwable) {
                isLoading.postValue(false)
                Log.e("EventsRepository", "onFailure: ${t.message}")
            }
        })
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