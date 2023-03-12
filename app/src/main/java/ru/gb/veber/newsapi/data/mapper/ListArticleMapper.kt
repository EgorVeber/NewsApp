package ru.gb.veber.newsapi.data.mapper
import ru.gb.veber.newsapi.common.extentions.formatDate
import ru.gb.veber.newsapi.common.extentions.formatDateTime
import ru.gb.veber.newsapi.common.extentions.stringFromData
import ru.gb.veber.newsapi.common.extentions.stringFromDataTime
import ru.gb.veber.newsapi.domain.models.Article
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder

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
