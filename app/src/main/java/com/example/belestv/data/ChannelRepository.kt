package com.example.belestv.data

import android.content.Context

/**
 * Kanal listesinin tek giriş noktası.
 *
 * VARSAYILAN LİSTE: Uygulama ilk açıldığında birden fazla halka açık,
 * yasal, şifresiz (free-to-air) kanal listesini birleştirir:
 * Türkiye kanalları + uluslararası genel/haber/spor/film/belgesel kanalları
 * + TRT'nin resmi yayın linkleri (garanti olsun diye ayrıca eklenmiştir).
 * Kaynak: iptv-org (https://github.com/iptv-org/iptv) topluluk projesi
 * ve TRT'nin kendi resmi CDN adresleri.
 * Yayınların güncelliğini ve yasallığını kendin teyit etmelisin.
 */
object ChannelRepository {

    private val DEFAULT_PLAYLIST_URLS = listOf(
        "https://iptv-org.github.io/iptv/countries/tr.m3u",
        "https://iptv-org.github.io/iptv/categories/general.m3u",
        "https://iptv-org.github.io/iptv/categories/news.m3u",
        "https://iptv-org.github.io/iptv/categories/sports.m3u",
        "https://iptv-org.github.io/iptv/categories/movies.m3u",
        "https://iptv-org.github.io/iptv/categories/documentary.m3u"
    )

    // TRT'nin bilinen resmi yayın linkleri (iptv-org listesinde bazen
    // kırık/eksik olabildiği için ayrıca ve öncelikli olarak eklenir).
    private fun officialTrtChannels(): List<Channel> = listOf(
        Channel(
            id = "trt-1",
            name = "TRT 1",
            logoUrl = null,
            group = "TRT",
            streamUrl = "https://trt.daioncdn.net/trt-1/master.m3u8?app=web"
        ),
        Channel(
            id = "trt-haber",
            name = "TRT Haber",
            logoUrl = null,
            group = "TRT",
            streamUrl = "https://trt.daioncdn.net/trt-haber/master.m3u8?app=web"
        ),
        Channel(
            id = "trt-spor",
            name = "TRT Spor",
            logoUrl = null,
            group = "TRT",
            streamUrl = "https://trt.daioncdn.net/trt-spor/master.m3u8?app=web"
        ),
        Channel(
            id = "trt-spor-yildiz",
            name = "TRT Spor Yıldız",
            logoUrl = null,
            group = "TRT",
            streamUrl = "https://trt.daioncdn.net/trt-spor-yildiz/master.m3u8?app=web"
        ),
        Channel(
            id = "trt-cocuk",
            name = "TRT Çocuk",
            logoUrl = null,
            group = "TRT",
            streamUrl = "https://trt.daioncdn.net/trt-cocuk/master.m3u8?app=web"
        ),
        Channel(
            id = "trt-belgesel",
            name = "TRT Belgesel",
            logoUrl = null,
            group = "TRT",
            streamUrl = "https://trt.daioncdn.net/trt-belgesel/master.m3u8?app=web"
        ),
        Channel(
            id = "trt-muzik",
            name = "TRT Müzik",
            logoUrl = null,
            group = "TRT",
            streamUrl = "https://trt.daioncdn.net/trt-muzik/master.m3u8?app=web"
        ),
        Channel(
            id = "trt-2",
            name = "TRT 2",
            logoUrl = null,
            group = "TRT",
            streamUrl = "https://trt.daioncdn.net/trt-2/master.m3u8?app=web"
        ),
        Channel(
            id = "trt-world",
            name = "TRT World",
            logoUrl = null,
            group = "TRT",
            streamUrl = "https://tv-trtworld.medya.trt.com.tr/master.m3u8"
        ),
        Channel(
            id = "trt-avaz",
            name = "TRT Avaz",
            logoUrl = null,
            group = "TRT",
            streamUrl = "https://trt.daioncdn.net/trt-avaz/master.m3u8?app=web"
        ),
        Channel(
            id = "trt-kurdi",
            name = "TRT Kurdî",
            logoUrl = null,
            group = "TRT",
            streamUrl = "https://trt.daioncdn.net/trt-kurdi/master.m3u8?app=web"
        )
    )

    private const val PREFS = "belestv_prefs"
    private const val KEY_FAVORITES = "favorites"
    private const val KEY_LAST_CHANNEL = "last_channel_id"
    private const val KEY_CUSTOM_M3U = "custom_m3u_url"

    fun loadDefaultChannels(): List<Channel> {
        val allChannels = mutableListOf<Channel>()
        val seenUrls = mutableSetOf<String>()

        // TRT kanallarını önce ekle, öncelikli ve garantili olsun
        for (channel in officialTrtChannels()) {
            if (seenUrls.add(channel.streamUrl)) {
                allChannels.add(channel)
            }
        }

        for (url in DEFAULT_PLAYLIST_URLS) {
            try {
                val channels = M3uParser.parseFromUrl(url)
                for (channel in channels) {
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
