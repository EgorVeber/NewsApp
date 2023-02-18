package ru.gb.veber.newsapi.utils.mapper
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.utils.formatDate
import ru.gb.veber.newsapi.utils.formatDateTime
import ru.gb.veber.newsapi.utils.stringFromData
import ru.gb.veber.newsapi.utils.stringFromDataTime
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.BaseViewHolder

const val SHOW_HISTORY = "SHOW_HISTORY"

fun List<Article>.toNewListArticleGroupByDate(): MutableList<Article> {
    val mutableList: MutableList<Article> = mutableListOf()
    this.reversed().map { article ->
        article.publishedAtChange = stringFromData(article.publishedAt).formatDateTime()
        article.dateAdded = stringFromDataTime(article.dateAdded.toString()).formatDate()
        article
    }.sortedBy { reversedArticle ->
        reversedArticle.dateAdded
    }.reversed().groupBy { groupArticle ->
        groupArticle.dateAdded
    }.forEach { group ->
        mutableList.add(getNewArticleHistory((group.key.toString()), group.value.size))
        group.value.reversed().forEach { groupReversedArticle ->
            mutableList.add(groupReversedArticle.apply {
                viewType = BaseViewHolder.VIEW_TYPE_HISTORY_NEWS
            })
        }
    }
    return mutableList
}
