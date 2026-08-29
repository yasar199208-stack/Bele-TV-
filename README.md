# Beleş TV (örnek Android TV IPTV Player)

Bu proje, "Beleş TiVi" tarzı bir Android TV / mobil IPTV player uygulaması için başlangıç iskeletidir. Android Studio'da açıp derleyebilirsin.

## Özellikler
- Kumandayla (D-Pad) gezilebilen kategori bazlı kanal listesi (Leanback)
- M3U / M3U8 playlist parser (kendi listeni veya iptv-org gibi kaynakları okuyabilir)
- Xtream Codes API desteği (kullanıcı kendi hesabını girebilir)
- ExoPlayer (Media3) ile HLS yayın oynatma
- Favoriler, son izlenen kanaldan devam etme
- Ayarlar ekranından özel M3U/Xtream linki ekleme

## Kurulum
1. Android Studio (Koala veya üstü) ile bu klasörü aç.
2. Gradle sync tamamlansın.
3. Bir Android TV emülatörü veya gerçek cihazda çalıştır.

## ÖNEMLİ — Kanal Kaynağı Hakkında
`ChannelRepository.DEFAULT_PLAYLIST_URL` şu an örnek olarak iptv-org
(https://github.com/iptv-org/iptv) topluluk projesinin Türkiye listesine
işaret ediyor. Bu proje halka açık, şifresiz (free-to-air) yayınları
derliyor ancak:

- Yayıncıların bu şekilde dağıtıma **açıkça izin verdiğini** kendin teyit
  etmelisin. Ülkeye ve kanala göre durum değişebilir.
- Ücretli/şifreli yayınları (spor paketleri, dizi/film platformları vb.)
  bu şekilde dağıtmak **telif hakkı ihlalidir** ve bu tür bir kullanım
  için destek veremem.
- Uygulamayı yayınlamadan önce (Play Store'a yüklemeden önce) tüm
  varsayılan kanalların yayın hakları ve lisans durumu konusunda emin ol.

## Eksikler / Genişletilebilecek Yerler
- EPG (yayın akışı) entegrasyonu için XMLTV parser eklenmeli
- Kategori gizleme ayarı (settings'e checkbox listesi)
- Görsel logo/ikon/banner placeholder — gerçek marka görselleriyle değiştirilmeli
- ProGuard/R8 kuralları, hata yönetimi (network timeout, boş liste vb.)
