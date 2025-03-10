package com.example.ujiandicoding.data.retrofit


import com.example.ujiandicoding.data.response.ListEventResponse
import com.example.ujiandicoding.data.response.ListEventsItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getListEvents(): Call<ListEventResponse>

    @GET("events")
    fun getUpcomingEvents(@Query("active") active: Int = 1): Call<ListEventResponse>

    @GET("events")
    fun getFinishedEvents(@Query("active") active: Int = 0): Call<ListEventResponse>


}