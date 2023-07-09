package ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder

import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener
import ru.gb.veber.ui_common.hide
import ru.gb.veber.ui_common.show
import ru.gb.veber.ui_core.databinding.TopNewsItemHeaderBinding
import ru.gb.veber.ui_core.extentions.loadPicForTitle

class NewsHeaderViewHolder(
    private val binding: TopNewsItemHeaderBinding,
    var listener: TopNewsListener,
) : BaseViewHolder(binding.root) {

    override fun bind(item: ArticleUiModel) = with(binding) {
        title.text = item.title
        publishedAt.text = item.publishedAtUi
        imageNews.loadPicForTitle(item.urlToImage)
        if (item.isHistory || item.isFavorites) viewedTextHeader.show() else viewedTextHeader.hide()
        root.setOnClickListener {
            listener.clickNews(item)
        }
    }
}
