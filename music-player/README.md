# 簡易音樂播放器

使用 Kotlin + Jetpack Compose + ExoPlayer 製作的 Android 音樂播放器。

## 功能

- 從裝置 MediaStore 讀取本機音樂
- 播放清單瀏覽（封面、歌名、歌手、時長）
- 播放控制：播放/暫停、上一首/下一首、進度條
- 背景播放與通知列控制
- 鎖定畫面媒體控制

## 環境需求

- Android Studio Ladybug (2024.2.1) 或更新版本
- JDK 17
- Android SDK 34
- 最低支援 API 26 (Android 8.0)

## 建置方式

1. 使用 **Android Studio** 開啟 `music-player` 資料夾
2. 等待 Gradle 同步完成
3. 連接實機或啟動模擬器
4. 點選 Run 執行

或使用指令列（需先有 Gradle wrapper）：

```bash
./gradlew assembleDebug
```

## 權限

- `READ_MEDIA_AUDIO`（Android 13+）或 `READ_EXTERNAL_STORAGE`（舊版）：讀取音樂檔案
- `POST_NOTIFICATIONS`（Android 13+）：顯示播放通知
- `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_MEDIA_PLAYBACK`：背景播放

## 專案結構

```
app/src/main/java/com/example/musicplayer/
├── MainActivity.kt
├── MusicPlayerApplication.kt
├── player/
│   ├── MusicPlayerService.kt   # 前景服務、ExoPlayer、MediaSession
│   └── PlayerState.kt           # MusicTrack、PlayerState 資料類
├── repository/
│   └── MusicRepository.kt      # MediaStore 查詢
└── ui/
    ├── PlayerScreen.kt          # 播放器畫面
    ├── PlayerViewModel.kt       # 播放狀態管理
    ├── PlaylistScreen.kt        # 播放清單
    └── components/
        └── PlayerControls.kt    # 控制按鈕與進度條
```

## 測試

將音樂檔案（如 MP3）複製到裝置的 `Music` 或 `Download` 資料夾，開啟 App 後應能載入並播放。
