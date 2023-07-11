package ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder

import androidx.core.view.isVisible
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener
import ru.gb.veber.ui_common.hide
import ru.gb.veber.ui_core.databinding.FavoritesHistoryItemBinding
import ru.gb.veber.ui_core.extentions.loadPicForCard

class FavoritesHistoryViewHolder(
    private val binding: FavoritesHistoryItemBinding,
    var listener: TopNewsListener,
) : BaseViewHolder(binding.root) {

    override fun bind(item: ArticleUiModel) = with(binding) {
        imageFavorites.isVisible = itemViewType == VIEW_TYPE_FAVORITES_NEWS
        deleteFavorites.isVisible = itemViewType == VIEW_TYPE_HISTORY_NEWS

        viewedText.hide()

        title.text = item.title
        publishedAt.text = item.publishedAtUi
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

