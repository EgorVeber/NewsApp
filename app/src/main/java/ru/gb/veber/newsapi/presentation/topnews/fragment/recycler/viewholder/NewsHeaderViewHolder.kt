package ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder

import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.NullRequestDataException
import coil.transform.RoundedCornersTransformation
import ru.gb.veber.newsapi.R
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
        imageNews.load(item.urlToImage) {
            transformations(RoundedCornersTransformation(20f))
            listener(
                onSuccess = { _, _ ->  /* do nothing */ },
                onError = { _: ImageRequest, _: ErrorResult ->
                    imageNews.setBackgroundResource(R.drawable.no_image_big)
                })
        }
        if (item.isHistory || item.isFavorites) viewedTextHeader.show() else viewedTextHeader.hide()
        root.setOnClickListener {
            listener.clickNews(item)
        }
    }
}
