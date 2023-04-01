package ru.gb.veber.newsapi.presentation.keymanagement.recycler

import androidx.recyclerview.widget.DiffUtil
import ru.gb.veber.newsapi.domain.models.ApiKeysModel

class KeysDiffUtil(
    private val oldItems: List<ApiKeysModel>,
    private val newItems: List<ApiKeysModel>,
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].id == newItems[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].actived == newItems[newItemPosition].actived
    }
}





