package com.example.ujiandicoding.data.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ujiandicoding.data.response.ListEventsItem
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Events (
    @PrimaryKey(autoGenerate = true)
    @field:ColumnInfo(name = "id")
    var id: Int ,

    @field:ColumnInfo(name = "name")
    var name: String? = null,

    @field:ColumnInfo(name = "description")
    var description: String? = null,

    @field:ColumnInfo(name = "image")
    var image: String? = null,

    @field:ColumnInfo(name = "beginTime")
    var beginTime: String? = null,

    @field:ColumnInfo(name = "endTime")
    var endTime: String? = null,

    @field:ColumnInfo(name = "isFavo")
    var isFavo: Boolean,

    @field:ColumnInfo(name = "summary")
    var summary: String? = null,

    @field:ColumnInfo(name = "ownerName")
    var ownerName: String? = null,

    @field:ColumnInfo(name = "mediaCover")
    var mediaCover: String? = null,

    @field:ColumnInfo(name = "imageLogo")
    var imageLogo: String? = null

): Parcelable

