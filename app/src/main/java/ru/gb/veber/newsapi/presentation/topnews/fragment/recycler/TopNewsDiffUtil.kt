package ru.gb.veber.newsapi.presentation.topnews.fragment.recycler

import androidx.recyclerview.widget.DiffUtil
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel

class TopNewsDiffUtil(
    private val oldItems: List<ArticleUiModel>,
    private val newItems: List<ArticleUiModel>,
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


