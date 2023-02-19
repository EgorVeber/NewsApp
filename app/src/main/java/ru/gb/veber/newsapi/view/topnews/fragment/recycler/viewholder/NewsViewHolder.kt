package ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder

import ru.gb.veber.newsapi.databinding.TopNewsItemBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.utils.hide
import ru.gb.veber.newsapi.utils.loadGlide
import ru.gb.veber.newsapi.utils.show
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.TopNewsListener

class NewsViewHolder(
    private val binding: TopNewsItemBinding,
    var listener: TopNewsListener,
) : BaseViewHolder(binding.root) {

    override fun bind(item: Article) = with(binding) {
        title.text = item.title
        publishedAt.text = item.publishedAtChange
        imageNews.loadGlide(item.urlToImage)
        if (item.isHistory || item.isFavorites) viewedText.show() else viewedText.hide()
        root.setOnClickListener {
            listener.clickNews(item)
        }
    }
}
