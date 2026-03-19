package com.example.musicplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.musicplayer.player.MusicTrack
import com.example.musicplayer.player.PlayerState
import com.example.musicplayer.ui.components.PlayerControls
import com.example.musicplayer.ui.components.SeekBar
import com.example.musicplayer.ui.components.TimeLabel

@Composable
fun PlayerScreen(
    playerState: PlayerState,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val track = playerState.currentTrack
    if (track == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "請從播放清單選擇歌曲",
                color = Color.White.copy(alpha = 0.6f)
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1a1a2e),
                        Color(0xFF16213e),
                        Color.Black
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (track.albumArtUri != null) {
                AsyncImage(
                    model = track.albumArtUri,
                    contentDescription = track.album,
                    modifier = Modifier
                        .size(280.dp)
                        .padding(16.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .padding(16.dp)
                        .background(Color.White.copy(alpha = 0.1f))
                )
            }
            Text(
                text = track.title,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1
            )
            Text(
                text = track.artist,
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SeekBar(
                currentPosition = playerState.currentPosition,
                duration = playerState.duration,
                onSeek = onSeek
            )
            Text(
                text = TimeLabel(playerState.currentPosition, playerState.duration),
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
            PlayerControls(
                isPlaying = playerState.isPlaying,
                onPlayPause = onPlayPause,
                onPrevious = onPrevious,
                onNext = onNext
            )
        }
    }
}
