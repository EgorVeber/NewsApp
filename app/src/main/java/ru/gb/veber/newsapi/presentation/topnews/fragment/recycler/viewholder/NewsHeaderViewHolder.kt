package ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder

import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.loadGlideNot
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.databinding.TopNewsItemHeaderBinding
import ru.gb.veber.newsapi.domain.models.Article
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener

class NewsHeaderViewHolder(
    private val binding: TopNewsItemHeaderBinding,
    var listener: TopNewsListener,
) : BaseViewHolder(binding.root) {

    override fun bind(item: Article) = with(binding) {
        title.text = item.title
        publishedAt.text = item.publishedAtChange
        imageNews.loadGlideNot(item.urlToImage)
        if (item.isHistory || item.isFavorites) viewedTextHeader.show() else viewedTextHeader.hide()
        root.setOnClickListener {
            listener.clickNews(item)
        }
    }
}
