package com.example.belestv.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.belestv.R
import com.example.belestv.data.Channel
import com.example.belestv.data.ChannelCache
import com.example.belestv.data.PlaybackState
import com.example.belestv.player.PlaybackActivity

/**
 * Kanal adına göre arama yapılan ekran. Ana ekranda yüklenmiş
 * kanal listesi üzerinde anlık filtreleme yapar.
 */
class SearchActivity : AppCompatActivity() {

    private var filtered: List<Channel> = emptyList()
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val input = findViewById<EditText>(R.id.search_input)
        listView = findViewById(R.id.search_results)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        listView.adapter = adapter

        filtered = ChannelCache.channels
        updateList("")

        input.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                updateList(s?.toString() ?: "")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        listView.setOnItemClickListener { _, _, position, _ ->
            val channel = filtered.getOrNull(position) ?: return@setOnItemClickListener
            PlaybackState.channels = filtered
            PlaybackState.startIndex = position
            startActivity(Intent(this, PlaybackActivity::class.java))
        }
    }

    private fun updateList(query: String) {
        filtered = if (query.isBlank()) {
            ChannelCache.channels
        } else {
            ChannelCache.channels.filter { it.name.contains(query, ignoreCase = true) }
        }
        adapter.clear()
        adapter.addAll(filtered.map { "${it.name}  (${it.group})" })
        adapter.notifyDataSetChanged()
    }
}
