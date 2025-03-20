package com.example.ujiandicoding.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Setting(
    @PrimaryKey val id: Int = 0,
    val notificationsEnabled: Boolean = false
)