package ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder

import ru.gb.veber.newsapi.databinding.HistoryArticleHeaderBinding
import ru.gb.veber.newsapi.domain.models.Article
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener

class HistoryHeaderViewHolder(
    private val binding: HistoryArticleHeaderBinding,
    var listener: TopNewsListener,
) : BaseViewHolder(binding.root) {

    override fun bind(item: Article) = with(binding) {
        dateAdded.text = item.publishedAt
        sizeNews.text = item.author
        dateAdded.setOnClickListener {
            listener.clickGroupHistory(item)
        }
        deleteHistoryGroup.setOnClickListener {
            listener.deleteGroupHistory(item)
        }
    }
}