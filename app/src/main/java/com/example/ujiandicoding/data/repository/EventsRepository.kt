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
import com.example.ujiandicoding.data.retrofit.ApiService
import com.example.ujiandicoding.data.utills.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class EventsRepository(application: Application, private val appExecutors: AppExecutors) {
    private val mEventsDao: EventsDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val apiService: ApiService = ApiConfig.getApiService()

    init {
        val db = EventsRoomDatabase.getDatabase(application)
        mEventsDao = db.eventsDao()
    }

    fun getAllEvents(): LiveData<List<Events>> = mEventsDao.getAllEvents()

    fun insert(events: Events) {
        appExecutors.diskIO.execute {
            mEventsDao.insert(events)
        }
    }

    fun delete(events: Events) {
        appExecutors.diskIO.execute {
            mEventsDao.delete(events)
        }
    }
    fun isEventFavorited(eventId: Int): LiveData<Boolean> {
        return mEventsDao.isEventFavorited(eventId)
    }
    fun getFavoriteEvents(): LiveData<List<Events>> {
        return mEventsDao.getFavoriteEvents()
    }
    fun update(events: Events) {
        appExecutors.diskIO.execute {
            mEventsDao.update(events)
        }
    }

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

//class EventsRepository (
//    application: Application,
//    private val eventsDao: EventsDao,
//    private val apiService: ApiService,
//    private val appExecutors: AppExecutors
//){
//    fun getUpcomingEvents(): LiveData<Result<List<Events>>> = liveData {
//        emit(Result.Loading)
//        try {
//            val response = apiService.getActiveEvents(BuildConfig.BASE_URL)
//            val events = response.listEvents
//            val eventsList = events.map { event ->
//                val isFavorited = eventsDao.isFavoriteEvent(event.id)
//                Events(
//                    event.id,
//                    event.name,
//                    event.description,
//                    event.imageLogo,
//                    event.beginTime,
//                    event.endTime,
//                    isFavorited
//                )
//            }
//            eventsDao.removeFromFavorites()
//            eventsDao.addToFavorites(eventsList)
//        }catch (e: Exception) {
//            Log.e("EventsRepository", "getUpcomingEvents: ${e.message.toString()}")
//            emit(Result.Error(e.message.toString()))
//        }
//        val localData: LiveData<Result<List<Events>>> =
//            eventsDao.getAllEvents().map { Result.Success(it) }
//        emitSource(localData)
//    }
//
//    fun getFinishedEvents(): LiveData<Result<List<Events>>> = liveData {
//        emit(Result.Loading)
//        try {
//            val response = apiService.getActiveEvents(BuildConfig.BASE_URL)
//            val events = response.listEvents
//            val eventsList = events.map { event ->
//                val isFavorited = eventsDao.isFavoriteEvent(event.id)
//                Events(
//                    event.id,
//                    event.name,
//                    event.description,
//                    event.imageLogo,
//                    event.beginTime,
//                    event.endTime,
//                    isFavorited
//                )
//            }
//            eventsDao.removeFromFavorites()
//            eventsDao.addToFavorites(eventsList)
//        }catch (e: Exception) {
//            Log.e("EventsRepository", "getUpcomingEvents: ${e.message.toString()}")
//            emit(Result.Error(e.message.toString()))
//        }
//        val localData: LiveData<Result<List<Events>>> =
//            eventsDao.getAllEvents().map { Result.Success(it) }
//        emitSource(localData)
//    }
//
//    fun getFavoriteEvents(): LiveData<Result<List<Events>>> {
//        return eventsDao.getFavoriteEvents().map { Result.Success(it) }
//    }
//
//    suspend fun setFavoriteEvents(events: Events, favoriteState: Boolean) {
//        events.isFavo = favoriteState
//        eventsDao.update(events)
//    }





