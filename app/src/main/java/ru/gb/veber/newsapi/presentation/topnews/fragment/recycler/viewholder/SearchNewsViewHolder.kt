package ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder

import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.NullRequestDataException
import coil.transform.RoundedCornersTransformation
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.loadGlide
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.databinding.SearchNewsItemBinding
import ru.gb.veber.newsapi.domain.models.Article
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener

class SearchNewsViewHolder(
    private val binding: SearchNewsItemBinding,
    var listener: TopNewsListener,
) : BaseViewHolder(binding.root) {

    override fun bind(item: Article) = with(binding) {
        title.text = item.title
        publishedAt.text = item.publishedAtChange
        imageNews.load(item.urlToImage) {
            transformations(RoundedCornersTransformation(20f))
            listener(
                onSuccess = { _, _ ->  /* do nothing */ },
                onError = { _: ImageRequest, error: ErrorResult ->
                    val image = if (error.throwable is NullRequestDataException) R.drawable.trdz_no_image_alter
                    else R.drawable.no_image
                    imageNews.setBackgroundResource(image)
                })
        }
        if (item.isHistory || item.isFavorites) viewedText.show() else viewedText.hide()
        root.setOnClickListener {
            listener.clickNews(item)
        }
    }
}
