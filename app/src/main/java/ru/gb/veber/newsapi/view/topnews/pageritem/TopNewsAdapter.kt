package ru.gb.veber.newsapi.view.topnews.pageritem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.databinding.TopNewsItemBinding
import ru.gb.veber.newsapi.databinding.TopNewsItemHeaderBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.utils.loadGlide
import ru.gb.veber.newsapi.utils.loadGlideNot
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder.Companion.VIEW_TYPE_FAVORITES_NEWS
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder.Companion.VIEW_TYPE_HEADER_NEWS
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder.Companion.VIEW_TYPE_NEWS

typealias OnUserClickListener = (article: Article) -> Unit

interface RecyclerListener {
    fun clickNews(article: Article)
    fun deleteFavorites(article: Article)
}

class TopNewsAdapter(
    var listener: RecyclerListener,
) : RecyclerView.Adapter<BaseViewHolder>() {

    var articles: List<Article> = listOf()
        set(value) {
            var diffUtil = TopNewsDiffUtil(field, value)
            val diffResult = DiffUtil.calculateDiff(diffUtil)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NEWS -> NewsViewHolder(TopNewsItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false), listener)
            VIEW_TYPE_FAVORITES_NEWS -> NewsViewHolder(TopNewsItemBinding.inflate(LayoutInflater.from(
                parent.context),
                parent,
                false), listener)
            VIEW_TYPE_HEADER_NEWS -> NewsHeaderViewHolder(TopNewsItemHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false), listener)
            else -> NewsViewHolder(
                TopNewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                listener)
        }
    }

    override fun getItemCount() = articles.size

    override fun getItemViewType(position: Int): Int {
        return articles[position].viewType
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(articles[position])
    }
}

class NewsViewHolder(
    private val binding: TopNewsItemBinding,
    var listener: RecyclerListener,
) : BaseViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    override fun bind(item: Article) = with(binding) {
        imageFavorites.visibility =
            if (itemViewType == VIEW_TYPE_FAVORITES_NEWS) View.VISIBLE else View.GONE
        title.text = item.title
        publishedAt.text = item.publishedAtChange
        imageNews.loadGlide(item.urlToImage)
        viewedText.text = if (item.isHistory || item.isFavorites) "viewed" else ""
        root.setOnClickListener {
            // onUserClickListener.invoke(item)
            listener.clickNews(item)
        }
        imageFavorites.setOnClickListener {
            listener.deleteFavorites(item)
        }
    }
}


class NewsHeaderViewHolder(
    private val binding: TopNewsItemHeaderBinding,
    var listener: RecyclerListener,
) : BaseViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    override fun bind(item: Article) = with(binding) {
        title.text = item.title
        publishedAt.text = item.publishedAtChange
        imageNews.loadGlideNot(item.urlToImage)
        viewedTextHeader.text = if (item.isHistory || item.isFavorites) "viewed" else ""
        root.setOnClickListener {
            // onUserClickListener.invoke(item)
            listener.clickNews(item)
        }
    }
}


abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: Article)

    companion object {
        const val VIEW_TYPE_NEWS = 0
        const val VIEW_TYPE_HEADER_NEWS = 1
        const val VIEW_TYPE_FAVORITES_NEWS = 2
    }
}


//1 варинат без scaleType обрезается часть содержимого
//        imageNews.load(item.urlToImage) {
//            transformations(RoundedCornersTransformation(25f))
//            placeholder(R.drawable.loading1)
//            error(R.drawable.plaecehodler2)
//        }
//2 варинат без scaleType загружает не обрезанную но мелькая относительно контейнера

