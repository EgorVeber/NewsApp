package ru.gb.veber.newsapi.view.topnews.pageritem

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.databinding.TopNewsItemBinding
import ru.gb.veber.newsapi.databinding.TopNewsItemHeaderBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.utils.loadGlide
import ru.gb.veber.newsapi.utils.loadGlideNot
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder.Companion.VIEW_TYPE_FAVORITES_NEWS
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder.Companion.VIEW_TYPE_HEADER_NEWS
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder.Companion.VIEW_TYPE_NEWS

typealias OnUserClickListener2 = (article: Article) -> Unit

class TopNewsListAdapter(
    private val onUserClickListener: OnUserClickListener2,
) : ListAdapter<Article,BaseViewHolder>(TopNewsDiffUtilList()) {


    class TopNewsDiffUtilList : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem==newItem
        }
    }


    var articles: List<Article> = emptyList()
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
                false), onUserClickListener)
            VIEW_TYPE_FAVORITES_NEWS -> NewsViewHolder(TopNewsItemBinding.inflate(LayoutInflater.from(
                parent.context),
                parent,
                false), onUserClickListener)
            VIEW_TYPE_HEADER_NEWS -> NewsHeaderViewHolder(TopNewsItemHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false), onUserClickListener)
            else -> NewsViewHolder(
                TopNewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
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

//1 варинат без scaleType обрезается часть содержимого
//        imageNews.load(item.urlToImage) {
//            transformations(RoundedCornersTransformation(25f))
//            placeholder(R.drawable.loading1)
//            error(R.drawable.plaecehodler2)
//        }
//2 варинат без scaleType загружает не обрезанную но мелькая относительно контейнера

