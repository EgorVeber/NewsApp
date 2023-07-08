package ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder

import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.loadPicForCard
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.databinding.SearchNewsItemBinding
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener

class SearchNewsViewHolder(
    private val binding: SearchNewsItemBinding,
    var listener: TopNewsListener,
) : BaseViewHolder(binding.root) {

    override fun bind(item: ArticleUiModel) = with(binding) {
        title.text = item.title
        publishedAt.text = item.publishedAtUi
        imageNews.loadPicForCard(item.urlToImage)
        if (item.isHistory || item.isFavorites) viewedText.show() else viewedText.hide()
        root.setOnClickListener {
            listener.clickNews(item)
        }
    }
}
