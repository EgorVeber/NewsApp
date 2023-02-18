package ru.gb.veber.newsapi.utils.mapper

import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

fun List<Article>.toListArticleUI(): List<Article> {
    return this.map { article->
        val publishedDate = stringFromData(article.publishedAt)
        val simpleFormat = SimpleDateFormat(FORMAT_DATE)

        val todayStr = App.instance.applicationContext.getString(R.string.today)
        val yesterdayStr = App.instance.applicationContext.getString(R.string.yesterday)
        val readOnStr = App.instance.applicationContext.getString(R.string.readOn)
        val anonymousOnStr = App.instance.applicationContext.getString(R.string.anonymous_author)

        if (simpleFormat.format(publishedDate).equals(simpleFormat.format(Date()))) {
            article.publishedAtChange = "$todayStr ${publishedDate.formatHour()}"
        } else if (simpleFormat.format(publishedDate)
                .equals(simpleFormat.format(takeDate(-1)))
        ) {
            article.publishedAtChange = "$yesterdayStr ${publishedDate.formatHour()}"
        } else {
            article.publishedAtChange = publishedDate.formatDateTime()
        }

        if (article.title == null || article.description.equals("")) {
            article.title = article.publishedAt
        }
        if (article.source.id == null) {
            article.source.id = ""
        }

        if (article.description.equals("") || article.description == null) {
            article.description = " $readOnStr ${article.source.name}"

        } else {
            article.description += "."
        }

        if (article.author == null || article.author.equals("") || article.author.equals(" ")) {
            article.author = anonymousOnStr
        }
        article
    }
}

fun List<Article>.toNewListArticleGroupByDate(): MutableList<Article> {
    val mutableList: MutableList<Article> = mutableListOf()
    this.reversed().map {
        it.publishedAtChange = stringFromData(it.publishedAt).formatDateTime()
        it.dateAdded = stringFromDataTime(it.dateAdded.toString()).formatDate()
        it
    }.sortedBy { it.dateAdded }.reversed().groupBy { it.dateAdded }.forEach { group ->
        mutableList.add(mapToArticleTitle((group.key.toString()),
            group.value.size))
        group.value.reversed().forEach {
            mutableList.add(it.apply {
                it.viewType = BaseViewHolder.VIEW_TYPE_HISTORY_NEWS
            })
        }
    }
    return mutableList
}