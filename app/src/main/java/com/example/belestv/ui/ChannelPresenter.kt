package com.example.belestv.ui

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import coil.load
import com.example.belestv.data.Channel

/**
 * Her kanalı gösterişli bir kart (logo + isim) olarak render eder.
 * Kumandayla odaklanınca büyüyüp öne çıkar (Leanback varsayılan davranışı).
 */
class ChannelPresenter : Presenter() {

    companion object {
        private const val CARD_WIDTH = 313
        private const val CARD_HEIGHT = 176
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as Channel
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = channel.name
        cardView.contentText = channel.group
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)

        if (!channel.logoUrl.isNullOrBlank()) {
            cardView.mainImageView.load(channel.logoUrl)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.badgeImage = null
        cardView.mainImage = null
    }
}
