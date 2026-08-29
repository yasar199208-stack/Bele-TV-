package com.example.belestv.data

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

/**
 * Standart M3U / M3U8 (Extended) playlist formatını parse eder.
 *
 * Örnek satır formatı:
 * #EXTINF:-1 tvg-id="trt1" tvg-logo="https://.../trt1.png" group-title="Ulusal",TRT 1
 * https://example.com/stream/trt1.m3u8
 *
 * Hem uzak bir URL'den (kullanıcının kendi M3U/Xtream linki) hem de
 * cihazdaki yerel bir dosyadan okuyabilir.
 */
object M3uParser {

    private val client = OkHttpClient()

    /** Uzak bir M3U linkinden (http/https) kanal listesi çeker. */
    fun parseFromUrl(url: String): List<Channel> {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: return emptyList()
            return parseContent(body)
        }
    }

    /** Cihaz üzerindeki yerel bir .m3u dosyasından kanal listesi okur. */
    fun parseFromLocalFile(filePath: String): List<Channel> {
        val text = BufferedReader(InputStreamReader(java.io.FileInputStream(filePath))).readText()
        return parseContent(text)
    }

    fun parseContent(content: String): List<Channel> {
        val lines = content.lines()
        val channels = mutableListOf<Channel>()

        var pendingName: String? = null
        var pendingLogo: String? = null
        var pendingGroup: String = "Diğer"

        for (rawLine in lines) {
            val line = rawLine.trim()
            when {
                line.startsWith("#EXTINF") -> {
                    pendingLogo = Regex("tvg-logo=\"([^\"]*)\"").find(line)?.groupValues?.get(1)
                    pendingGroup = Regex("group-title=\"([^\"]*)\"").find(line)?.groupValues?.get(1)
                        ?: "Diğer"
                    // İsim, son virgülden sonraki kısımdır
                    pendingName = line.substringAfterLast(",").trim()
                }
                line.isNotBlank() && !line.startsWith("#") -> {
                    // Bu satır stream URL'i
                    channels.add(
                        Channel(
                            id = UUID.randomUUID().toString(),
                            name = pendingName ?: "Bilinmeyen Kanal",
                            logoUrl = pendingLogo,
                            group = pendingGroup,
                            streamUrl = line
                        )
                    )
                    pendingName = null
                    pendingLogo = null
                    pendingGroup = "Diğer"
                }
            }
        }
        return channels
    }
}
