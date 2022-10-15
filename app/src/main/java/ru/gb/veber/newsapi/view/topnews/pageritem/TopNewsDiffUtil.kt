package ru.gb.veber.newsapi.view.topnews.pageritem

import android.util.Log
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
        Log.d("areContentsTheSame", "old" + (oldItems[oldItemPosition].isHistory).toString())
        Log.d("areContentsTheSame", "new" + (newItems[newItemPosition].isHistory).toString())
        return oldItems[oldItemPosition].isHistory != newItems[newItemPosition].isHistory
    }


    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val old = oldItems[oldItemPosition]
        val new = newItems[newItemPosition]
        return Change(old, new)
    }
}

data class Change<out T>(
    val oldData: T,
    val newData: T
)

fun <T> createCombinedPayload(payloads: List<Change<T>>): Change<T> {
    assert(payloads.isNotEmpty())
    val firstChange = payloads.first()
    val lastChange = payloads.last()
    return Change(firstChange.oldData, lastChange.newData)
}



