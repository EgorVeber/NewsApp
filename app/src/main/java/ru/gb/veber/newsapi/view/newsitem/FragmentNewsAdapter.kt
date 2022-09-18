package ru.gb.veber.newsapi.view.viewpagernews

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.databinding.NewsItemBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.ArticleDTO
import ru.gb.veber.newsapi.utils.loadGlide

typealias OnUserClickListener = (article: Article) -> Unit

class FragmentNewsAdapter(
    private val onUserClickListener: OnUserClickListener,
) : RecyclerView.Adapter<NewsViewHolder>() {

    var articles: List<Article> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onUserClickListener)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
//        if (position == (getItemCount() - 1)) {
//        }
        holder.bind(articles[position])
    }

    override fun getItemCount() = articles.size
}

class NewsViewHolder(
    private val binding: NewsItemBinding, private val onUserClickListener: OnUserClickListener,
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(item: Article) = with(binding) {
//        author.text = item.author
//        description.text = item.description
//        source.text = item.source.name + " ;" + item.source.id
//        url.text = item.url

        title.text = item.title
        publishedAt.text = item.publishedAtChange

        //1 варинат без scaleType обрезается часть содержимого
//        imageNews.load(item.urlToImage) {
//            transformations(RoundedCornersTransformation(25f))
//            placeholder(R.drawable.loading1)
//            error(R.drawable.plaecehodler2)
//        }
        //2 варинат без scaleType загружает не обрезанную но мелькая относительно контейнера
        imageNews.loadGlide(item.urlToImage)
        root.setOnClickListener {
            onUserClickListener.invoke(item)
        }
    }
}
