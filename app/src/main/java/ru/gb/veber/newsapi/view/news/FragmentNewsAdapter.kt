package ru.gb.veber.newsapi.view.news

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.databinding.NewsItemBinding
import ru.gb.veber.newsapi.databinding.SourcesItemBinding
import ru.gb.veber.newsapi.model.data.ArticleDTO
import ru.gb.veber.newsapi.model.data.SourcesDTO
import ru.gb.veber.newsapi.utils.loadGlide


class FragmentNewsAdapter() : RecyclerView.Adapter<NewsViewHolder>() {

    var articles: List<ArticleDTO> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount() = articles.size
}

class NewsViewHolder(
    private val binding: NewsItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(item: ArticleDTO) = with(binding) {
        author.text = item.author
        description.text = item.description
        publishedAt.text = item.publishedAt
        source.text = item.source.name + " ;" + item.source.id
        title.text = item.title
        url.text = item.url
        imageNews.load(item.urlToImage){
            placeholder(R.drawable.default_icon)
        }
    }
}
