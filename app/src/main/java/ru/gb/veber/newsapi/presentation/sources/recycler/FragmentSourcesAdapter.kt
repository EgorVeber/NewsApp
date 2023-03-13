package ru.gb.veber.newsapi.view.sources

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.databinding.SourcesItemBinding
import ru.gb.veber.newsapi.domain.models.Sources
import ru.gb.veber.newsapi.presentation.sources.recycler.SourcesDiffUtil
import ru.gb.veber.newsapi.presentation.sources.recycler.SourcesListener
import ru.gb.veber.newsapi.presentation.sources.recycler.SourcesViewHolder

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
            listener
        )
    }

    override fun onBindViewHolder(holder: SourcesViewHolder, position: Int) {
        holder.bind(sources[position])
    }

    override fun getItemCount() = sources.size
}
