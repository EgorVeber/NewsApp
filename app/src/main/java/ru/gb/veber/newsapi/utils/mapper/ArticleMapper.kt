package ru.gb.veber.newsapi.utils.mapper

import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.utils.FORMAT_DATE
import ru.gb.veber.newsapi.utils.formatDate
import ru.gb.veber.newsapi.utils.formatDateTime
import ru.gb.veber.newsapi.utils.formatHour
import ru.gb.veber.newsapi.utils.mapToArticleTitle
import ru.gb.veber.newsapi.utils.stringFromData
import ru.gb.veber.newsapi.utils.stringFromDataTime
import ru.gb.veber.newsapi.utils.takeDate
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.Date

fun List<Article>.toListArticleUI(): List<Article> {
    return this.map { article->
        val publishedDate = stringFromData(article.publishedAt)
        val simpleFormat = SimpleDateFormat(FORMAT_DATE)

        val todayStr = App.instance.applicationContext.getString(R.string.today)
        val yesterdayStr = App.instance.applicationContext.getString(R.string.yesterday)
        val readOnStr = App.instance.applicationContext.getString(R.string.readOn)
        val anonymousOnStr = App.instance.applicationContext.getString(R.string.anonymousAuthor)

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