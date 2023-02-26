package ru.gb.veber.newsapi.view.topnews.fragment.recycler

import androidx.recyclerview.widget.DiffUtil
import ru.gb.veber.newsapi.model.Article

class TopNewsDiffUtil(
    private val oldItems: List<Article>,
    private val newItems: List<Article>,
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].title == newItems[newItemPosition].title
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].isHistory != newItems[newItemPosition].isHistory
    }
}


