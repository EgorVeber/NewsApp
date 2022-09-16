package ru.gb.veber.newsapi.view.sources

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.databinding.SourcesItemBinding
import ru.gb.veber.newsapi.model.SourcesDTO


class FragmentSourcesAdapter() : RecyclerView.Adapter<SourcesViewHolder>() {

    var sources: List<SourcesDTO> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourcesViewHolder {
        return SourcesViewHolder(
            SourcesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SourcesViewHolder, position: Int) {
        holder.bind(sources[position])
    }

    override fun getItemCount() = sources.size
}

class SourcesViewHolder(
    private val binding: SourcesItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SourcesDTO) = with(binding) {
        idSources.text = item.id
        name.text = item.name
        description.text = item.description
        url.text = item.url
        category.text = item.category
        language.text = item.language
        country.text = item.country
    }
}
