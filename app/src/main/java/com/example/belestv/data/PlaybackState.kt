package com.example.belestv.data

/**
 * Oynatma ekranına geçerken kanal listesini bellekte tutar.
 * Büyük listeleri Intent üzerinden taşımak Android'in izin verdiği
 * boyutu aşıp çökmeye sebep olabildiği için bu yöntem kullanılır.
 */
object PlaybackState {
    var channels: List<Channel> = emptyList()
    var startIndex: Int = 0
}
