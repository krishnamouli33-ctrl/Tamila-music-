package com.example.data

import kotlinx.coroutines.flow.Flow

class SongRepository(private val songDao: SongDao) {
    val savedSongs: Flow<List<Song>> = songDao.getSavedSongs()

    suspend fun saveSong(song: Song) {
        songDao.insertSong(song.copy(isSaved = true, savedAt = System.currentTimeMillis()))
    }

    suspend fun removeSong(id: String) {
        songDao.deleteSong(id)
    }
    
    suspend fun isSaved(id: String): Boolean {
        return songDao.getSong(id)?.isSaved == true
    }
}
