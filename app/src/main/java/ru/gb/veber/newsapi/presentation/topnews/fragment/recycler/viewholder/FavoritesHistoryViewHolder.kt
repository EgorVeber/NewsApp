package ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder

import android.view.View
import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.loadPicForCard
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.databinding.FavoritesHistoryItemBinding
import ru.gb.veber.newsapi.domain.models.Article
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener

class FavoritesHistoryViewHolder(
    private val binding: FavoritesHistoryItemBinding,
    var listener: TopNewsListener,
) : BaseViewHolder(binding.root) {

    override fun bind(item: Article) = with(binding) {

        imageFavorites.visibility =
            if (itemViewType == VIEW_TYPE_FAVORITES_NEWS) View.VISIBLE else View.GONE

        deleteFavorites.visibility =
            if (itemViewType == VIEW_TYPE_HISTORY_NEWS) View.VISIBLE else View.GONE

        if (item.isHistory || item.isFavorites) viewedText.show() else viewedText.hide()

        title.text = item.title
        publishedAt.text = item.publishedAtChange
        imageNews.loadPicForCard(item.urlToImage)

        root.setOnClickListener {
            listener.clickNews(item)
        }

        imageFavorites.setOnClickListener {
            listener.deleteFavorites(item)
        }

        deleteFavorites.setOnClickListener {
            listener.deleteHistory(item)
        }
    }
}

