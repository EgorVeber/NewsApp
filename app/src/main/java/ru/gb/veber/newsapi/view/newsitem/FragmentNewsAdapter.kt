package ru.gb.veber.newsapi.view.viewpagernews

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.databinding.NewsItemBinding
import ru.gb.veber.newsapi.databinding.NewsItemHeaderBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.utils.loadGlide
import ru.gb.veber.newsapi.utils.loadGlideNot
import ru.gb.veber.newsapi.view.viewpagernews.BaseViewHolder.Companion.VIEW_TYPE_HEADER_NEWS
import ru.gb.veber.newsapi.view.viewpagernews.BaseViewHolder.Companion.VIEW_TYPE_NEWS

typealias OnUserClickListener = (article: Article) -> Unit

class FragmentNewsAdapter(
    private val onUserClickListener: OnUserClickListener,
) : RecyclerView.Adapter<BaseViewHolder>() {

    var articles: List<Article> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NEWS -> NewsViewHolder(NewsItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false), onUserClickListener)
            VIEW_TYPE_HEADER_NEWS -> NewsHeaderViewHolder(NewsItemHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false), onUserClickListener)
            else -> NewsViewHolder(
                NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onUserClickListener)
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
    private val binding: NewsItemBinding,
    private val onUserClickListener: OnUserClickListener,
) : BaseViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    override fun bind(item: Article) = with(binding) {
        title.text = item.title
        publishedAt.text = item.publishedAtChange
        imageNews.loadGlide(item.urlToImage)
        root.setOnClickListener {
            onUserClickListener.invoke(item)
        }
    }
}


class NewsHeaderViewHolder(
    private val binding: NewsItemHeaderBinding,
    private val onUserClickListener: OnUserClickListener,
) : BaseViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    override fun bind(item: Article) = with(binding) {
        title.text = item.title
        publishedAt.text = item.publishedAtChange
        imageNews.loadGlideNot(item.urlToImage)
        root.setOnClickListener {
            onUserClickListener.invoke(item)
        }
    }
}


abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: Article)

    companion object {
        const val VIEW_TYPE_NEWS = 0
        const val VIEW_TYPE_HEADER_NEWS = 1
    }
}


//1 варинат без scaleType обрезается часть содержимого
//        imageNews.load(item.urlToImage) {
//            transformations(RoundedCornersTransformation(25f))
//            placeholder(R.drawable.loading1)
//            error(R.drawable.plaecehodler2)
//        }
//2 варинат без scaleType загружает не обрезанную но мелькая относительно контейнера

