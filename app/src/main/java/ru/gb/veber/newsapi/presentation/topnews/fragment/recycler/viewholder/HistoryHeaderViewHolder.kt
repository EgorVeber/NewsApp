package ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder

import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener
import ru.gb.veber.ui_core.databinding.HistoryArticleHeaderBinding

class HistoryHeaderViewHolder(
    private val binding: HistoryArticleHeaderBinding,
    var listener: TopNewsListener,
) : BaseViewHolder(binding.root) {

    override fun bind(item: ArticleUiModel) = with(binding) {
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
