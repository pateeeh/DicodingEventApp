package com.example.ujiandicoding.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EventsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(events: Events)

    @Delete
    suspend fun delete(events: Events)

    @Query("SELECT * from events ORDER BY id ASC")
    fun getAllEvents(): LiveData<List<Events>>

    @Query("SELECT EXISTS(SELECT 1 FROM events WHERE id = :eventId)")
    suspend fun isEventFavorited(eventId: Int): Boolean

    @Query("SELECT * FROM events WHERE isFavo = 1")
    fun getFavoriteEvents(): LiveData<List<Events>>

    @Update
    suspend fun update(events: Events)

    @Query("SELECT * FROM events WHERE name LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%'")
    fun searchEvents(keyword: String): LiveData<List<Events>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(events: List<Events>)
}