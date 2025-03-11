package com.example.ujiandicoding.data.retrofit

import com.example.ujiandicoding.data.response.ListEventResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getListEvents(): ListEventResponse

    @GET("events")
    suspend fun getUpcomingEvents(@Query("active") active: Int = 1): ListEventResponse

    @GET("events")
    suspend fun getFinishedEvents(@Query("active") active: Int = 0): ListEventResponse
}