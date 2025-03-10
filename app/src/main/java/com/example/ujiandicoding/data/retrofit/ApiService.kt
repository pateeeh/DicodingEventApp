package com.example.ujiandicoding.data.retrofit


import com.example.ujiandicoding.data.response.ListEventResponse
import com.example.ujiandicoding.data.response.ListEventsItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getListEvents(): Call<ListEventResponse>

    @GET("events?active=1")
    fun getUpcomingEvents(): Call<ListEventResponse>

    @GET("events?active=0")
    fun getFinishedEvents(): Call<ListEventResponse>

}