package com.example.ujiandicoding.data.retrofit

import com.example.ujiandicoding.data.response.ListEventResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getListEvents(): Response<ListEventResponse>

    @GET("events")
    suspend fun getUpcomingEvents(@Query("active") active: Int = 1): Response<ListEventResponse>

    @GET("events")
    suspend fun getFinishedEvents(@Query("active") active: Int = 0): Response<ListEventResponse>
}