package ru.gb.veber.newsapi.presentation.favorites.delegate

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.gb.veber.newsapi.presentation.models.ArticleHistoryHeader
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder
import ru.gb.veber.ui_common.hide
import ru.gb.veber.ui_core.databinding.FavoritesHistoryItemBinding
import ru.gb.veber.ui_core.databinding.HistoryArticleHeaderBinding
import ru.gb.veber.ui_core.extentions.loadPicForCard

object DelegateAdapter {

    fun getArticleHistoryDelegate(listener: TopNewsListener) =
        adapterDelegateViewBinding<ArticleUiModel, ListItem, FavoritesHistoryItemBinding>(
            { layoutInflater, parent ->
                FavoritesHistoryItemBinding.inflate(layoutInflater, parent, false)
            }
        ) {
            bind {
                with(binding) {
                    imageFavorites.isVisible =
                        item.viewType == BaseViewHolder.VIEW_TYPE_FAVORITES_NEWS
                    deleteFavorites.isVisible =
                        item.viewType == BaseViewHolder.VIEW_TYPE_HISTORY_NEWS

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
        }

    fun getArticleHistoryHeaderDelegate(itemClickedListener : (String) -> Unit) =
        adapterDelegateViewBinding<ArticleHistoryHeader, ListItem, HistoryArticleHeaderBinding>(
            { layoutInflater, parent ->
                HistoryArticleHeaderBinding.inflate(layoutInflater, parent, false)
            }
        ) {
            bind {
                with(binding) {
                    dateAdded.text = item.dateAdded
                    sizeNews.text = item.sizeArticle
                    dateAdded.setOnClickListener {
                        // listener.clickGroupHistory(item)
                    }
                    deleteHistoryGroup.setOnClickListener {
                        itemClickedListener.invoke(item.dateAdded)
                    }
                }
            }
        }
}