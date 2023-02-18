package ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder

import ru.gb.veber.newsapi.databinding.HistoryArticleHeaderBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.TopNewsListener

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
