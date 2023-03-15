package ru.gb.veber.newsapi.presentation.search.recycler

import androidx.recyclerview.widget.DiffUtil
import ru.gb.veber.newsapi.domain.models.HistorySelect

class HistoryDiffUtil(
    private val oldItems: List<HistorySelect>,
    private val newItems: List<HistorySelect>,
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].id == newItems[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].id == newItems[newItemPosition].id
    }
}





