package com.example.belestv.player

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.belestv.R
import com.example.belestv.data.Channel
import com.example.belestv.data.ChannelRepository

/**
 * Kanal oynatma ekranı. Ekrana dokunulunca çıkan kontrol çubuğundaki
 * ileri/geri düğmeleriyle sıradaki/önceki kanala geçilebilir.
 */
class PlaybackActivity : ComponentActivity() {

    private var player: ExoPlayer? = null
    private var channels: List<Channel> = emptyList()
    private lateinit var channelNameView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableFullScreen()

        setContentView(R.layout.activity_playback)

                channels = com.example.belestv.data.PlaybackState.channels
        val startIndex = com.example.belestv.data.PlaybackState.startIndex
            .coerceIn(0, (channels.size - 1).coerceAtLeast(0))

        if (channels.isEmpty()) return

        val playerView = findViewById<PlayerView>(R.id.player_view)
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        channelNameView = findViewById(R.id.channel_name_overlay)

        val mediaItems = channels.map { channel ->
            MediaItem.Builder()
                .setUri(channel.streamUrl)
                .setMediaId(channel.id)
                .build()
        }

        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            playerView.player = exoPlayer
            exoPlayer.setMediaItems(mediaItems, startIndex, 0L)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true

            exoPlayer.addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    updateChannelName(mediaItem?.mediaId)
                    mediaItem?.mediaId?.let {
                        ChannelRepository.saveLastWatched(this@PlaybackActivity, it)
                    }
                }
            })
        }

        updateChannelName(channels.getOrNull(startIndex)?.id)
    }

    private fun updateChannelName(channelId: String?) {
        val channel = channels.firstOrNull { it.id == channelId }
        channelNameView.text = channel?.name ?: ""
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
