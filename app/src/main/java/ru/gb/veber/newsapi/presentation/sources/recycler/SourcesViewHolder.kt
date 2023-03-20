package ru.gb.veber.newsapi.presentation.sources.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.databinding.SourcesItemBinding
import ru.gb.veber.newsapi.domain.models.Sources

class SourcesViewHolder(
    private val binding: SourcesItemBinding,
    private val listener: SourcesListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Sources) = with(binding) {

        if (item.liked) imageFavorites.setImageResource(R.drawable.ic_favorite_36_active)
        else imageFavorites.setImageResource(R.drawable.ic_favorite_36)

        name.text = item.name
        description.text = item.description
        category.text = item.category
        language.text = item.language
        country.text = item.country
        totalFavorites.text = item.totalFavorites.toString()
        totalHistory.text = item.totalHistory.toString()

        root.setOnClickListener {
            groupAbout.visibility = if (groupAbout.visibility == View.VISIBLE) View.GONE
            else View.VISIBLE
        }

        openNewsSources.setOnClickListener {
            listener.newsClick(item.idSources, item.name)
        }

        openWebSiteSources.setOnClickListener {
            listener.openUrl(item.url)
        }

        clickFavorites.setOnClickListener {
            listener.imageClick(item)
        }
    }
}
