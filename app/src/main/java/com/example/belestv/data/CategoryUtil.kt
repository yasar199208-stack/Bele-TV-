package com.example.belestv.data

/**
 * İngilizce kategori isimlerini Türkçeye çeviren ortak yardımcı.
 * MainFragment ve PlaybackActivity tarafından paylaşılır.
 */
object CategoryUtil {
    private val CATEGORY_TR = mapOf(
        "entertainment" to "Eğlence",
        "news" to "Haber",
        "general" to "Genel",
        "sports" to "Spor",
        "religious" to "Dini",
        "kids" to "Çocuk",
        "movies" to "Film",
        "music" to "Müzik",
        "documentary" to "Belgesel",
        "education" to "Eğitim",
        "lifestyle" to "Yaşam Tarzı",
        "culture" to "Kültür",
        "business" to "Ekonomi",
        "science" to "Bilim",
        "travel" to "Gezi",
        "weather" to "Hava Durumu",
        "auto" to "Otomobil",
        "cooking" to "Yemek",
        "classic" to "Klasik",
        "comedy" to "Komedi",
        "family" to "Aile",
        "legislative" to "Meclis",
        "local" to "Yerel",
        "outdoor" to "Doğa",
        "relax" to "Dinlenme",
        "series" to "Dizi",
        "shop" to "Alışveriş",
        "trt" to "TRT",
        "undefined" to "Diğer"
    )

    fun translate(group: String): String {
        return CATEGORY_TR[group.trim().lowercase()] ?: group
    }
}
