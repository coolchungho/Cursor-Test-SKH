package com.example.musicplayer.player

import android.net.Uri

/**
 * 單曲資料模型
 */
data class MusicTrack(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: Uri,
    val albumArtUri: Uri? = null
)

/**
 * 播放狀態
 */
data class PlayerState(
    val currentTrack: MusicTrack? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val playlist: List<MusicTrack> = emptyList(),
    val currentIndex: Int = 0
)
