package com.example.ujiandicoding.data.response

import com.google.gson.annotations.SerializedName


data class ListEventResponse(

	@field:SerializedName("listEvents")
	val listEvents: List<ListEventsItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)