package com.example.belestv.data

/**
 * Tek bir kanalı temsil eder. M3U playlist'ten parse edilir.
 */
data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val group: String,
    val streamUrl: String,
    var isFavorite: Boolean = false
) : java.io.Serializable
