package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Song
import com.example.ui.components.SongItem
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    currentSong: Song?,
    onPlayClick: (Song) -> Unit
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showDonateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tamila Tamila", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { showDonateDialog = true }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Support", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading && searchResults.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    item {
                        Text(
                            text = "Top Tamil Hits",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(searchResults) { song ->
                        SongItem(
                            song = song,
                            isPlaying = currentSong?.id == song.id,
                            onPlayClick = { onPlayClick(song) },
                            onSaveClick = { viewModel.toggleSaveSong(song) }
                        )
                    }
                }
            }
        }
        
        if (showDonateDialog) {
            AlertDialog(
                onDismissRequest = { showDonateDialog = false },
                title = { Text("Support the Creator") },
                text = { Text("Since Tamila Tamila is completely free with no ads or subscriptions, the app earns through direct community support to the owner's bank account. Thank you for your support!") },
                confirmButton = {
                    TextButton(onClick = { showDonateDialog = false }) {
                        Text("Donate via UPI")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDonateDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
