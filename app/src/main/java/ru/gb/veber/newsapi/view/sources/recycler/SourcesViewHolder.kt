package ru.gb.veber.newsapi.view.sources.recycler

import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.databinding.SourcesItemBinding
import ru.gb.veber.newsapi.model.Sources

class SourcesViewHolder(
    private val binding: SourcesItemBinding,
    private val listener: SourcesListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Sources) = with(binding) {

        if (item.isLike) imageFavorites.setImageResource(R.drawable.ic_favorite_36_active)
        else imageFavorites.setImageResource(R.drawable.ic_favorite_36)

        name.text = item.name
        description.text = item.description
        category.text = item.category
        language.text = item.language
        country.text = item.country
        totalFavorites.text = item.totalFavorites.toString()
        totalHistory.text = item.totalHistory.toString()


        openNewsSources.setOnClickListener {
            listener.newsClick(item.idSources, item.name)
        }

        openWebSiteSources.setOnClickListener {
            listener.openUrl(item.url)
        }

        imageFavorites.setOnClickListener {
            listener.imageClick(item)
        }
    }
}
