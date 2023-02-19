package ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.model.Article

abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: Article)

    companion object {
        const val VIEW_TYPE_TOP_NEWS = 0
        const val VIEW_TYPE_TOP_NEWS_HEADER = 1
        const val VIEW_TYPE_SEARCH_NEWS = 2
        const val VIEW_TYPE_FAVORITES_NEWS = 3
        const val VIEW_TYPE_HISTORY_NEWS = 4
        const val VIEW_TYPE_HISTORY_HEADER = 5
    }
}
