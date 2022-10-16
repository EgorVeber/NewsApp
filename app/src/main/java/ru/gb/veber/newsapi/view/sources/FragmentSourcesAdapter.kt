package ru.gb.veber.newsapi.view.sources

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.databinding.SourcesItemBinding
import ru.gb.veber.newsapi.model.Sources

interface SourcesListener {
    fun openUrl(url: String?)
    fun imageClick(source: Sources)
    fun newsClick(source: String?, name: String?)
}

class FragmentSourcesAdapter(
    var listener: SourcesListener,
) : RecyclerView.Adapter<SourcesViewHolder>() {

    var sources: List<Sources> = emptyList()
        set(value) {
            var diffUtil = SourcesDiffUtil(field, value)
            var diffResult = DiffUtil.calculateDiff(diffUtil)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourcesViewHolder {
        return SourcesViewHolder(
            SourcesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            listener)
    }

    override fun onBindViewHolder(holder: SourcesViewHolder, position: Int) {
        holder.bind(sources[position])
    }

    override fun getItemCount() = sources.size
}

class SourcesViewHolder(
    private val binding: SourcesItemBinding,
    private val listener: SourcesListener,
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(item: Sources) = with(binding) {

        if (item.isLike) imageFavorites.setImageResource(R.drawable.ic_favorite_36_active)
        else imageFavorites.setImageResource(R.drawable.ic_favorite_36)

        name.text = item.name
        description.text = item.description
        url.text = item.url
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
