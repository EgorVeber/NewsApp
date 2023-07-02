package ru.gb.veber.newsapi.presentation.sources.recycler

import androidx.recyclerview.widget.DiffUtil
import ru.gb.veber.newsapi.domain.models.SourcesModel

class SourcesDiffUtil(
    private val oldItems: List<SourcesModel>,
    private val newItems: List<SourcesModel>,
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].id == newItems[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].liked != newItems[newItemPosition].liked
    }
}





