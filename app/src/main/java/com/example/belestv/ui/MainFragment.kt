package com.example.belestv.ui

import android.content.Intent
import android.os.Bundle
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.example.belestv.data.Channel
import com.example.belestv.data.ChannelRepository
import com.example.belestv.player.PlaybackActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Uygulama açılışındaki ana ekran: sol üstte kategoriler (Ulusal, Haber, Spor...),
 * her kategori için yatay kayan gösterişli kanal kartları.
 * Tamamen kumanda/D-Pad ile gezilebilir (Leanback'in doğal davranışı).
 */
class MainFragment : BrowseSupportFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = "Beleş TV"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = 0xFF1A1A2E.toInt()

        setupEventListeners()
        loadChannels()
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is Channel) {
                val intent = Intent(activity, PlaybackActivity::class.java)
                intent.putExtra("channel_url", item.streamUrl)
                intent.putExtra("channel_name", item.name)
                intent.putExtra("channel_id", item.id)
                startActivity(intent)
            }
        }
    }

    private fun loadChannels() {
        CoroutineScope(Dispatchers.Main).launch {
            val channels = withContext(Dispatchers.IO) {
                // Önce kullanıcının kendi eklediği liste var mı bak, yoksa varsayılanı yükle
                activity?.let { ChannelRepository.loadCustomChannels(it) }
                    ?: ChannelRepository.loadDefaultChannels()
            }
            buildRows(channels)
        }
    }

    private fun buildRows(channels: List<Channel>) {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val presenter = ChannelPresenter()

        // Kanalları kategoriye (group-title) göre grupla, düzenli satırlar oluştur
        val grouped = channels.groupBy { it.group }

        // Favoriler her zaman en üstte
        val favIds = activity?.let { ChannelRepository.getFavorites(it) } ?: emptySet()
        val favoriteChannels = channels.filter { it.id in favIds }
        if (favoriteChannels.isNotEmpty()) {
            val favAdapter = ArrayObjectAdapter(presenter)
            favAdapter.addAll(0, favoriteChannels)
            rowsAdapter.add(ListRow(HeaderItem(0, "⭐ Favoriler"), favAdapter))
        }

        grouped.entries.forEachIndexed { index, (group, groupChannels) ->
            val listRowAdapter = ArrayObjectAdapter(presenter)
            listRowAdapter.addAll(0, groupChannels)
            rowsAdapter.add(ListRow(HeaderItem(index.toLong() + 1, group), listRowAdapter))
        }

        adapter = rowsAdapter
    }
}
