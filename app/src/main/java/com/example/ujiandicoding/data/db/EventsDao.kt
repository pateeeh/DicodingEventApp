package com.example.ujiandicoding.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface EventsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(events: Events)

    @Delete
    fun delete(events: Events)

    @Query("SELECT * from events ORDER BY id ASC")
    fun getAllEvents(): LiveData<List<Events>>

    @Query("SELECT EXISTS(SELECT 1 FROM events WHERE id = :eventId)")
    fun isEventFavorited(eventId: Int): LiveData<Boolean>

    @Query("SELECT * FROM events WHERE isFavo = 1")
    fun getFavoriteEvents(): LiveData<List<Events>>

    @Update
    fun update(events: Events)
}