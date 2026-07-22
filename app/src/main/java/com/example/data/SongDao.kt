package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs WHERE isSaved = 1 ORDER BY savedAt DESC")
    fun getSavedSongs(): Flow<List<Song>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deleteSong(id: String)
    
    @Query("SELECT * FROM songs WHERE id = :id LIMIT 1")
    suspend fun getSong(id: String): Song?
}
