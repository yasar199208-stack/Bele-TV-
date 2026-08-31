package com.example.belestv.data

/**
 * Ana ekranda yüklenen tüm kanalları bellekte tutar,
 * arama ekranı bu listeyi tekrar ağdan çekmeden kullanır.
 */
object ChannelCache {
    var channels: List<Channel> = emptyList()
}
