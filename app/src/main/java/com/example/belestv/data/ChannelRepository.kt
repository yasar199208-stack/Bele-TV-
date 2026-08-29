package com.example.belestv.data

import android.content.Context

/**
 * Kanal listesinin tek giriş noktası.
 *
 * VARSAYILAN LİSTE: Uygulama ilk açıldığında DEFAULT_PLAYLIST_URL'den
 * genel/yasal, şifresiz (free-to-air) kanalları çeker. Bu proje, örnek olarak
 * iptv-org (https://github.com/iptv-org/iptv) topluluk kaynağını referans alır —
 * bu proje ülkelere göre halka açık, lisans bilgisi etiketlenmiş listeler tutar.
 * Kendi listeni kullanmak istersen aşağıdaki URL'yi değiştir ya da
 * Ayarlar ekranından kendi M3U/Xtream bilgini gir.
 */
object ChannelRepository {

    // Örnek: iptv-org projesinin Türkiye kanalları için topladığı liste.
    // Yayınların güncelliğini ve yasallığını kendin teyit etmelisin.
    const val DEFAULT_PLAYLIST_URL = "https://iptv-org.github.io/iptv/countries/tr.m3u"

    private const val PREFS = "belestv_prefs"
    private const val KEY_FAVORITES = "favorites"
    private const val KEY_LAST_CHANNEL = "last_channel_id"
    private const val KEY_CUSTOM_M3U = "custom_m3u_url"

    fun loadDefaultChannels(): List<Channel> {
        return M3uParser.parseFromUrl(DEFAULT_PLAYLIST_URL)
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
