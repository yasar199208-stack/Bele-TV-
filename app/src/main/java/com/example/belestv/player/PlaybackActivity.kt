package com.example.belestv.player

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.belestv.R
import com.example.belestv.data.CategoryUtil
import com.example.belestv.data.Channel
import com.example.belestv.data.ChannelRepository
import com.example.belestv.data.PlaybackState
import com.example.belestv.ui.SettingsActivity

/**
 * Kanal oynatma ekranı. Üstte kanal adı ve ayarlar/çıkış butonları,
 * altta oynat/duraklat/ses kontrolü, kategori sekmeleri ve kanal
 * seçim satırıyla gösterişli bir arayüz sunar.
 */
class PlaybackActivity : ComponentActivity() {

    private var player: ExoPlayer? = null
    private var channels: List<Channel> = emptyList()
    private var selectedCategory: String = "Tümü"
    private var isMuted = false

    private lateinit var channelNameView: TextView
    private lateinit var playPauseButton: Button
    private lateinit var muteButton: Button
    private lateinit var categoryTabs: LinearLayout
    private lateinit var channelRow: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableFullScreen()

        setContentView(R.layout.activity_playback)

        channels = PlaybackState.channels
        val startIndex = PlaybackState.startIndex
            .coerceIn(0, (channels.size - 1).coerceAtLeast(0))

        if (channels.isEmpty()) {
            finish()
            return
        }

        val playerView = findViewById<PlayerView>(R.id.player_view)
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

        channelNameView = findViewById(R.id.channel_name_overlay)
        playPauseButton = findViewById(R.id.btn_play_pause)
        muteButton = findViewById(R.id.btn_mute)
        categoryTabs = findViewById(R.id.category_tabs)
        channelRow = findViewById(R.id.channel_row)

        findViewById<Button>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        findViewById<Button>(R.id.btn_exit).setOnClickListener { finish() }
        playPauseButton.setOnClickListener { togglePlayPause() }
        muteButton.setOnClickListener { toggleMute() }

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
                    renderChannelRow()
                }
            })
        }

        updateChannelName(channels.getOrNull(startIndex)?.id)
        buildCategoryTabs()
        renderChannelRow()
    }

    private fun currentChannelId(): String? {
        val index = player?.currentMediaItemIndex ?: return null
        return channels.getOrNull(index)?.id
    }

    private fun updateChannelName(channelId: String?) {
        val channel = channels.firstOrNull { it.id == channelId }
        channelNameView.text = channel?.name ?: ""
    }

    private fun togglePlayPause() {
        val exoPlayer = player ?: return
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            playPauseButton.text = "▶ Oynat"
        } else {
            exoPlayer.play()
            playPauseButton.text = "⏸ Duraklat"
        }
    }

    private fun toggleMute() {
        val exoPlayer = player ?: return
        isMuted = !isMuted
        exoPlayer.volume = if (isMuted) 0f else 1f
        muteButton.text = if (isMuted) "🔇 Sessiz" else "🔊 Ses Açık"
    }

    private fun buildCategoryTabs() {
        val categories = mutableListOf("Tümü")
        categories.addAll(channels.map { it.group }.distinct().sorted())

        categoryTabs.removeAllViews()
        for (category in categories) {
            val label = if (category == "Tümü") "Tümü" else CategoryUtil.translate(category)
            val button = Button(this).apply {
                text = label
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(24, 8, 24, 8)
                setBackgroundColor(
                    if (category == selectedCategory) 0xFFE94560.toInt() else 0x331A1A2E
                )
                setOnClickListener {
                    selectedCategory = category
                    buildCategoryTabs()
                    renderChannelRow()
                }
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = 8
            categoryTabs.addView(button, params)
        }
    }

    private fun renderChannelRow() {
        val filtered = if (selectedCategory == "Tümü") {
            channels
        } else {
            channels.filter { it.group == selectedCategory }
        }

        val activeId = currentChannelId()
        channelRow.removeAllViews()
        for (channel in filtered) {
            val button = Button(this).apply {
                text = channel.name
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(24, 8, 24, 8)
                setBackgroundColor(
                    if (channel.id == activeId) 0xFFF5A623.toInt() else 0x331A1A2E
                )
                setOnClickListener {
                    val index = channels.indexOfFirst { it.id == channel.id }
                    if (index >= 0) {
                        player?.seekTo(index, 0L)
                        player?.play()
                    }
                }
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = 8
            channelRow.addView(button, params)
        }
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
