package com.example.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.example.musicplayer.player.MusicTrack
import com.example.musicplayer.repository.MusicRepository
import com.example.musicplayer.ui.PlaylistScreen
import com.example.musicplayer.ui.PlayerScreen
import com.example.musicplayer.ui.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@UnstableApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(background = Color.Black)) {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    MusicPlayerApp()
                }
            }
        }
    }
}

@UnstableApi
@Composable
fun MusicPlayerApp(
    viewModel: PlayerViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var tracks by remember { mutableStateOf<List<MusicTrack>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val playerState by viewModel.playerState.collectAsState()
    val context = LocalContext.current
    val musicRepository = remember { MusicRepository(context) }
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val audioGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            results[Manifest.permission.READ_MEDIA_AUDIO] == true
        } else {
            results[Manifest.permission.READ_EXTERNAL_STORAGE] == true
        }
        if (audioGranted) {
            isLoading = true
            scope.launch(Dispatchers.IO) {
                val loaded = musicRepository.loadLocalMusic()
                withContext(Dispatchers.Main) {
                    tracks = loaded
                    isLoading = false
                }
            }
        } else {
            isLoading = false
        }
    }


    DisposableEffect(context) {
        viewModel.connect(context)
        onDispose { }
    }

    LaunchedEffect(context) {
        if (hasAudioPermission(context)) {
            isLoading = true
            tracks = musicRepository.loadLocalMusic()
            isLoading = false
        } else {
            val permissions = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS
                )
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.POST_NOTIFICATIONS
                )
                else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            permissionLauncher.launch(permissions)
            isLoading = false
        }
    }


    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF1a1a2e),
            contentColor = Color.White
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("播放清單") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("正在播放") }
            )
        }
        Crossfade(
            targetState = selectedTab,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) { tab ->
            when (tab) {
                0 -> PlaylistScreen(
                    tracks = tracks,
                    isLoading = isLoading,
                    hasPermission = hasAudioPermission(context),
                    onTrackClick = { track ->
                        val index = tracks.indexOfFirst { it.id == track.id }
                            .takeIf { it >= 0 } ?: 0
                        viewModel.playTrack(track, tracks, index)
                        selectedTab = 1
                    }
                )
                1 -> PlayerScreen(
                    playerState = playerState,
                    onPlayPause = {
                        if (playerState.isPlaying) viewModel.pause()
                        else viewModel.play()
                    },
                    onPrevious = { viewModel.previous() },
                    onNext = { viewModel.next() },
                    onSeek = { viewModel.seekTo(it) }
                )
            }
        }
    }
}

private fun hasAudioPermission(context: android.content.Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
    } else {
        @Suppress("DEPRECATION")
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED
    }
}

