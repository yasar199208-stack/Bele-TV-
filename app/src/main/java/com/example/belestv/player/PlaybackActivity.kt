package com.example.belestv.player

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.belestv.R
import com.example.belestv.data.ChannelRepository

/**
 * Kanal oynatma ekranı. Tam ekran (immersive) modda çalışır,
 * durum çubuğu ve gezinme çubuğu gizlenir.
 */
class PlaybackActivity : ComponentActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ekranı uyanık tut ve tam ekran (immersive) moda geç
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableFullScreen()

        setContentView(R.layout.activity_playback)

        val streamUrl = intent.getStringExtra("channel_url") ?: return
        val channelId = intent.getStringExtra("channel_id")

        val playerView = findViewById<PlayerView>(R.id.player_view)
        // Videoyu ekrana tam sığdır (kenarlarda siyah şerit bırakmadan kırparak doldur)
        playerView.resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM

        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            playerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(streamUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }

        channelId?.let { ChannelRepository.saveLastWatched(this, it) }
    }

    private fun enableFullScreen() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
            )
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) enableFullScreen()
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
