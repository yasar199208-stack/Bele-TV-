package com.example.belestv.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.belestv.R
import com.example.belestv.data.ChannelRepository

/**
 * Kanal oynatma ekranı. D-Pad ile play/pause/CH+/CH- kontrolü PlayerView'in
 * kendi built-in kumanda desteğinden gelir (Leanback ExoPlayer entegrasyonu).
 */
class PlaybackActivity : ComponentActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)

        val streamUrl = intent.getStringExtra("channel_url") ?: return
        val channelId = intent.getStringExtra("channel_id")

        val playerView = findViewById<PlayerView>(R.id.player_view)
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            playerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(streamUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }

        // "Kaldığı yerden devam et" için son izlenen kanalı kaydet
        channelId?.let { ChannelRepository.saveLastWatched(this, it) }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
