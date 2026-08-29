package com.example.belestv.data

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

/**
 * Xtream Codes API üzerinden canlı kanal listesi çeken basit istemci.
 * Kullanıcı Ayarlar ekranından sunucu/kullanıcı/şifre bilgilerini girer.
 */
class XtreamClient(
    private val host: String,     // örn: http://sunucu.com:8080
    private val username: String,
    private val password: String
) {
    private val client = OkHttpClient()

    fun fetchLiveChannels(): List<Channel> {
        val url = "$host/player_api.php?username=$username&password=$password&action=get_live_streams"
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: return emptyList()
            val arr = JSONArray(body)
            val result = mutableListOf<Channel>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val streamId = obj.getInt("stream_id")
                val streamUrl = "$host/live/$username/$password/$streamId.m3u8"
                result.add(
                    Channel(
                        id = streamId.toString(),
                        name = obj.optString("name", "Kanal $streamId"),
                        logoUrl = obj.optString("stream_icon", null),
                        group = obj.optString("category_id", "Diğer"),
                        streamUrl = streamUrl
                    )
                )
            }
            return result
        }
    }
}
