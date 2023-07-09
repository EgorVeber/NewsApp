package ru.gb.veber.newsapi.presentation.topnews.fragment.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder.Companion.VIEW_TYPE_FAVORITES_NEWS
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder.Companion.VIEW_TYPE_HISTORY_HEADER
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder.Companion.VIEW_TYPE_HISTORY_NEWS
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder.Companion.VIEW_TYPE_SEARCH_NEWS
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder.Companion.VIEW_TYPE_TOP_NEWS
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder.Companion.VIEW_TYPE_TOP_NEWS_HEADER
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.FavoritesHistoryViewHolder
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.HistoryHeaderViewHolder
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.NewsHeaderViewHolder
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.NewsViewHolder
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.SearchNewsViewHolder
import ru.gb.veber.ui_core.databinding.FavoritesHistoryItemBinding
import ru.gb.veber.ui_core.databinding.HistoryArticleHeaderBinding
import ru.gb.veber.ui_core.databinding.SearchNewsItemBinding
import ru.gb.veber.ui_core.databinding.TopNewsItemBinding
import ru.gb.veber.ui_core.databinding.TopNewsItemHeaderBinding

class TopNewsAdapter(
    var listener: TopNewsListener,
) : RecyclerView.Adapter<BaseViewHolder>() {

    var articleModels: List<ArticleUiModel> = listOf()
        set(value) {
            val diffUtil = TopNewsDiffUtil(field, value)
            val diffResult = DiffUtil.calculateDiff(diffUtil)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_TOP_NEWS -> NewsViewHolder(
                TopNewsItemBinding.inflate(LayoutInflater.from(
                parent.context),
                parent,
                false), listener)
            VIEW_TYPE_TOP_NEWS_HEADER -> NewsHeaderViewHolder(
                TopNewsItemHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false), listener)
            VIEW_TYPE_SEARCH_NEWS -> SearchNewsViewHolder(
                SearchNewsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false), listener)

            VIEW_TYPE_FAVORITES_NEWS -> FavoritesHistoryViewHolder(
                FavoritesHistoryItemBinding.inflate(
                LayoutInflater.from(
                    parent.context),
                parent,
                false), listener)

            VIEW_TYPE_HISTORY_NEWS -> FavoritesHistoryViewHolder(FavoritesHistoryItemBinding.inflate(
                LayoutInflater.from(
                    parent.context),
                parent,
                false), listener)

            VIEW_TYPE_HISTORY_HEADER -> HistoryHeaderViewHolder(
                HistoryArticleHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false), listener)
            else -> NewsViewHolder(
                TopNewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                listener)
        }
    }

    override fun getItemCount() = articleModels.size

    override fun getItemViewType(position: Int): Int {
        return articleModels[position].viewType
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(articleModels[position])
    }
}
