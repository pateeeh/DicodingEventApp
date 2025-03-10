package com.example.ujiandicoding.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListEventsItem(

	@field:SerializedName("summary")
	val summary: String,

	@field:SerializedName("mediaCover")
	val mediaCover: String,

	@field:SerializedName("imageLogo")
	val imageLogo: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("ownerName")
	val ownerName: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("beginTime")
	val beginTime: String,

	@field:SerializedName("endTime")
	val endTime: String,
): Parcelable