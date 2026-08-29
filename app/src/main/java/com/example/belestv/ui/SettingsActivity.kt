package com.example.belestv.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.belestv.R
import com.example.belestv.data.ChannelRepository

/**
 * Kullanıcının kendi M3U linkini veya Xtream hesap bilgilerini girebildiği
 * ayarlar ekranı ("Upload Your Own List" özelliği).
 */
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val urlInput = findViewById<EditText>(R.id.input_m3u_url)
        val saveButton = findViewById<Button>(R.id.button_save)

        saveButton.setOnClickListener {
            val url = urlInput.text.toString().trim()
            if (url.isNotEmpty()) {
                ChannelRepository.saveCustomM3uUrl(this, url)
                Toast.makeText(this, "Liste kaydedildi, ana ekrana dönüp yenileyin", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}
