package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Song
import com.example.data.SongRepository
import com.example.network.RetrofitClient
import com.example.player.MusicPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: SongRepository,
    val playerManager: MusicPlayerManager
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Song>>(emptyList())
    val searchResults: StateFlow<List<Song>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("Tamil Hit Songs")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val savedSongs: StateFlow<List<Song>> = repository.savedSongs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        searchSongs("Tamil Hit Songs")
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        searchSongs(query)
    }

    fun searchSongs(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ensure we are searching for tamil songs by appending tamil to queries if not present
                val term = if (query.contains("tamil", ignoreCase = true)) query else "tamil $query"
                val response = RetrofitClient.instance.searchSongs(term = term)
                val songs = response.results.mapNotNull { track ->
                    if (track.previewUrl == null || track.trackName == null) null
                    else Song(
                        id = track.trackId.toString(),
                        title = track.trackName,
                        artist = track.artistName ?: "Unknown Artist",
                        album = track.collectionName ?: "Unknown Album",
                        albumArtUrl = track.artworkUrl100?.replace("100x100", "500x500") ?: "",
                        previewUrl = track.previewUrl,
                        isSaved = repository.isSaved(track.trackId.toString())
                    )
                }
                _searchResults.value = songs
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleSaveSong(song: Song) {
        viewModelScope.launch {
            if (repository.isSaved(song.id)) {
                repository.removeSong(song.id)
            } else {
                repository.saveSong(song)
            }
            // Update search results list to reflect saved status
            _searchResults.value = _searchResults.value.map { 
                if (it.id == song.id) it.copy(isSaved = !it.isSaved) else it 
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}
