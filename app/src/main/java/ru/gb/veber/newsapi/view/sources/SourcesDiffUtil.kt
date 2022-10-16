package ru.gb.veber.newsapi.view.sources

import androidx.recyclerview.widget.DiffUtil
import ru.gb.veber.newsapi.model.Sources

class SourcesDiffUtil(
    private val oldItems: List<Sources>,
    private val newItems: List<Sources>,
) : DiffUtil.Callback() {
    var positionScroll = 1
    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].id == newItems[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        positionScroll = newItemPosition
        return oldItems[oldItemPosition].isLike != newItems[newItemPosition].isLike
    }
}





