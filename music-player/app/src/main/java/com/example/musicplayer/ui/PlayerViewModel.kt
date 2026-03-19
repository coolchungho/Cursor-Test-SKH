package com.example.musicplayer.ui

import android.app.Application
import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayer.player.MusicTrack
import com.example.musicplayer.player.MusicPlayerService
import com.example.musicplayer.player.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 管理播放狀態與 MediaController 連線
 */
class PlayerViewModel(application: Application) : androidx.lifecycle.AndroidViewModel(application) {

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private var controller: MediaController? = null
    private var connectionJob: Job? = null
    private var positionUpdateJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            syncStateFromPlayer()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playerState.update { it.copy(isPlaying = isPlaying) }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            syncStateFromPlayer()
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            syncStateFromPlayer()
        }
    }

    /**
     * 連接至 MusicPlayerService
     */
    fun connect(context: Context) {
        // 啟動服務以確保背景播放可用
        val intent = android.content.Intent(context, MusicPlayerService::class.java)
        context.startForegroundService(intent)

        connectionJob?.cancel()
        connectionJob = viewModelScope.launch {
            try {
                val sessionToken = SessionToken(
                    context,
                    ComponentName(context, MusicPlayerService::class.java)
                )
                val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
                val mediaController = controllerFuture.get()
                withContext(Dispatchers.Main) {
                    setController(mediaController)
                }
            } catch (_: Exception) {
                // 連線失敗，可能服務尚未啟動
            }
        }
    }

    private fun setController(mediaController: MediaController) {
        controller?.removeListener(playerListener)
        controller = mediaController
        mediaController.addListener(playerListener)
        syncStateFromPlayer()
        startPositionUpdates()
    }

    private fun syncStateFromPlayer() {
        val c = controller ?: return
        val currentItem = c.currentMediaItem
        val track = currentItem?.localConfiguration?.tag as? MusicTrack
        val playlist = (0 until c.mediaItemCount).mapNotNull { i ->
            c.getMediaItemAt(i).localConfiguration?.tag as? MusicTrack
        }
        _playerState.update {
            it.copy(
                currentTrack = track,
                isPlaying = c.isPlaying,
                currentPosition = c.currentPosition,
                duration = c.duration.coerceAtLeast(0),
                playlist = playlist.ifEmpty { it.playlist },
                currentIndex = c.currentMediaItemIndex
            )
        }
    }

    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = viewModelScope.launch {
            while (true) {
                delay(500)
                controller?.let { c ->
                    if (c.isPlaying) {
                        _playerState.update {
                            it.copy(
                                currentPosition = c.currentPosition,
                                duration = c.duration.coerceAtLeast(0)
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * 播放指定歌曲與清單
     */
    fun playTrack(track: MusicTrack, playlist: List<MusicTrack>, startIndex: Int = 0) {
        val c = controller ?: return
        val effectivePlaylist = if (playlist.isEmpty()) listOf(track) else playlist
        val index = startIndex.coerceIn(0, (effectivePlaylist.size - 1).coerceAtLeast(0))
        val mediaItems = effectivePlaylist.map { t ->
            MediaItem.Builder()
                .setUri(t.uri)
                .setTag(t)
                .build()
        }
        c.setMediaItems(mediaItems, index, 0L)
        c.prepare()
        c.play()
    }

    fun play() {
        controller?.play()
    }

    fun pause() {
        controller?.pause()
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
    }

    fun next() {
        controller?.seekToNextMediaItem()
    }

    fun previous() {
        controller?.seekToPreviousMediaItem()
    }

    override fun onCleared() {
        controller?.removeListener(playerListener)
        controller?.release()
        controller = null
        connectionJob?.cancel()
        positionUpdateJob?.cancel()
        super.onCleared()
    }
}
