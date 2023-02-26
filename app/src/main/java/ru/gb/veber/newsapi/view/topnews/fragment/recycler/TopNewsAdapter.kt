package ru.gb.veber.newsapi.view.topnews.fragment.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.databinding.FavoritesHistoryItemBinding
import ru.gb.veber.newsapi.databinding.HistoryArticleHeaderBinding
import ru.gb.veber.newsapi.databinding.SearchNewsItemBinding
import ru.gb.veber.newsapi.databinding.TopNewsItemBinding
import ru.gb.veber.newsapi.databinding.TopNewsItemHeaderBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.BaseViewHolder
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.BaseViewHolder.Companion.VIEW_TYPE_FAVORITES_NEWS
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.BaseViewHolder.Companion.VIEW_TYPE_HISTORY_HEADER
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.BaseViewHolder.Companion.VIEW_TYPE_HISTORY_NEWS
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.BaseViewHolder.Companion.VIEW_TYPE_SEARCH_NEWS
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.BaseViewHolder.Companion.VIEW_TYPE_TOP_NEWS
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.BaseViewHolder.Companion.VIEW_TYPE_TOP_NEWS_HEADER
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.FavoritesHistoryViewHolder
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.HistoryHeaderViewHolder
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.NewsHeaderViewHolder
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.NewsViewHolder
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.SearchNewsViewHolder

class TopNewsAdapter(
    var listener: TopNewsListener,
) : RecyclerView.Adapter<BaseViewHolder>() {

    var articles: List<Article> = listOf()
        set(value) {
            var diffUtil = TopNewsDiffUtil(field, value)
            val diffResult = DiffUtil.calculateDiff(diffUtil)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_TOP_NEWS -> NewsViewHolder(TopNewsItemBinding.inflate(LayoutInflater.from(
                parent.context),
                parent,
                false), listener)
            VIEW_TYPE_TOP_NEWS_HEADER -> NewsHeaderViewHolder(TopNewsItemHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false), listener)
            VIEW_TYPE_SEARCH_NEWS -> SearchNewsViewHolder(SearchNewsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false), listener)

            VIEW_TYPE_FAVORITES_NEWS -> FavoritesHistoryViewHolder(FavoritesHistoryItemBinding.inflate(
                LayoutInflater.from(
                    parent.context),
                parent,
                false), listener)

            VIEW_TYPE_HISTORY_NEWS -> FavoritesHistoryViewHolder(FavoritesHistoryItemBinding.inflate(
                LayoutInflater.from(
                    parent.context),
                parent,
                false), listener)

            VIEW_TYPE_HISTORY_HEADER -> HistoryHeaderViewHolder(HistoryArticleHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false), listener)
            else -> NewsViewHolder(
                TopNewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                listener)
        }
    }

    override fun getItemCount() = articles.size

    override fun getItemViewType(position: Int): Int {
        return articles[position].viewType
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(articles[position])
    }
}
