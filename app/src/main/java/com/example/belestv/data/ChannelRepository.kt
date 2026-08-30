package com.example.belestv.data

import android.content.Context

/**
 * Kanal listesinin tek giriş noktası.
 *
 * VARSAYILAN LİSTE: Uygulama ilk açıldığında birden fazla halka açık,
 * yasal, şifresiz (free-to-air) kanal listesini birleştirir:
 * Türkiye kanalları + uluslararası genel/haber/spor/film/belgesel kanalları.
 * Kaynak: iptv-org (https://github.com/iptv-org/iptv) topluluk projesi.
 * Yayınların güncelliğini ve yasallığını kendin teyit etmelisin.
 * Kendi listeni kullanmak istersen Ayarlar ekranından kendi M3U/Xtream
 * bilgini girebilirsin.
 */
object ChannelRepository {

    // Birleştirilecek listeler: Türkiye + uluslararası kategoriler
    private val DEFAULT_PLAYLIST_URLS = listOf(
        "https://iptv-org.github.io/iptv/countries/tr.m3u",
        "https://iptv-org.github.io/iptv/categories/general.m3u",
        "https://iptv-org.github.io/iptv/categories/news.m3u",
        "https://iptv-org.github.io/iptv/categories/sports.m3u",
        "https://iptv-org.github.io/iptv/categories/movies.m3u",
        "https://iptv-org.github.io/iptv/categories/documentary.m3u"
    )

    private const val PREFS = "belestv_prefs"
    private const val KEY_FAVORITES = "favorites"
    private const val KEY_LAST_CHANNEL = "last_channel_id"
    private const val KEY_CUSTOM_M3U = "custom_m3u_url"

    fun loadDefaultChannels(): List<Channel> {
        val allChannels = mutableListOf<Channel>()
        val seenUrls = mutableSetOf<String>()

        for (url in DEFAULT_PLAYLIST_URLS) {
            try {
                val channels = M3uParser.parseFromUrl(url)
                for (channel in channels) {
                    // Aynı yayın linkine sahip tekrar eden kanalları atla
                    if (seenUrls.add(channel.streamUrl)) {
                        allChannels.add(channel)
                    }
                }
            } catch (e: Exception) {
                // Bir liste çekilemezse diğerlerine devam et, uygulamayı çökertme
            }
        }

        return allChannels
    }

    fun loadCustomChannels(context: Context): List<Channel>? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val customUrl = prefs.getString(KEY_CUSTOM_M3U, null) ?: return null
        return M3uParser.parseFromUrl(customUrl)
    }

    fun saveCustomM3uUrl(context: Context, url: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_CUSTOM_M3U, url).apply()
    }

    fun toggleFavorite(context: Context, channelId: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val favs = prefs.getStringSet(KEY_FAVORITES, emptySet())!!.toMutableSet()
        if (!favs.add(channelId)) favs.remove(channelId)
        prefs.edit().putStringSet(KEY_FAVORITES, favs).apply()
    }

    fun getFavorites(context: Context): Set<String> {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getStringSet(KEY_FAVORITES, emptySet())!!
    }

    fun saveLastWatched(context: Context, channelId: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_LAST_CHANNEL, channelId).apply()
    }

    fun getLastWatched(context: Context): String? {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LAST_CHANNEL, null)
    }
}
