package ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder

import android.view.View
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.loadGlide
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.databinding.TopNewsItemBinding
import ru.gb.veber.newsapi.domain.models.Article
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener

class NewsViewHolder(
    private val binding: TopNewsItemBinding,
    var listener: TopNewsListener,
) : BaseViewHolder(binding.root) {

    override fun bind(item: Article) = with(binding) {
        title.text = item.title
        publishedAt.text = item.publishedAtChange
        imageNews.show()
        imageNews.load(item.urlToImage) {
            listener(
                onSuccess = { _, _ ->
                    // do nothing
                },
                onError = { request: ImageRequest, throwable: ErrorResult ->
                    imageNews.hide() // За место сокрытия сюда можно будет поставить NOFILE
                })
        }//.loadGlide(item.urlToImage)
        if (item.isHistory || item.isFavorites) viewedText.show() else viewedText.hide()
        root.setOnClickListener {
            listener.clickNews(item)
        }
    }
}
