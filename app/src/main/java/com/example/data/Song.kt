package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtUrl: String,
    val previewUrl: String,
    val isSaved: Boolean = false,
    val savedAt: Long = 0L
)
