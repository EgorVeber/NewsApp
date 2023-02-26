package ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder

import ru.gb.veber.newsapi.databinding.SearchNewsItemBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.utils.extentions.hide
import ru.gb.veber.newsapi.utils.extentions.loadGlide
import ru.gb.veber.newsapi.utils.extentions.show
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.TopNewsListener

class SearchNewsViewHolder(
    private val binding: SearchNewsItemBinding,
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
