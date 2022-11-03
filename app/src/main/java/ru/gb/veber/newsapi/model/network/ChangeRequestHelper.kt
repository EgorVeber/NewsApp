package ru.gb.veber.newsapi.model.network

import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder.Companion.VIEW_TYPE_HISTORY_NEWS
import java.text.SimpleDateFormat
import java.util.*

object ChangeRequestHelper {

    fun changeRequest(list: List<Article>): List<Article> {
        return list.map {

            val publishedDate = stringFromData(it.publishedAt)
            val simpleFormat = SimpleDateFormat(FORMAT_DATE)

            val todayStr = App.instance.applicationContext.getString(R.string.today)
            val yesterdayStr = App.instance.applicationContext.getString(R.string.yesterday)
            val readOnStr = App.instance.applicationContext.getString(R.string.readOn)
            val anonymousOnStr = App.instance.applicationContext.getString(R.string.anonymousAuthor)

            if (simpleFormat.format(publishedDate).equals(simpleFormat.format(Date()))) {
                it.publishedAtChange = "$todayStr ${publishedDate.formatHour()}"
            } else if (simpleFormat.format(publishedDate)
                    .equals(simpleFormat.format(takeDate(-1)))
            ) {
                it.publishedAtChange = "$yesterdayStr ${publishedDate.formatHour()}"
            } else {
                it.publishedAtChange = publishedDate.formatDateTime()
            }

            if (it.title == null || it.description.equals("")) {
                it.title = it.publishedAt
            }
            if (it.source.id == null) {
                it.source.id = ""
            }

            if (it.description.equals("") || it.description == null) {
                it.description = " $readOnStr ${it.source.name}"

            } else {
                it.description += "."
            }

            if (it.author == null || it.author.equals("") || it.author.equals(" ")) {
                it.author = anonymousOnStr
            }
            it
        }
    }

    fun changeHistoryList(map: List<Article>): MutableList<Article> {
        val mutableList: MutableList<Article> = mutableListOf()
        map.reversed().map {
            it.publishedAtChange = stringFromData(it.publishedAt).formatDateTime()
            it.dateAdded = stringFromDataTime(it.dateAdded.toString()).formatDate()
            it
        }.sortedBy { it.dateAdded }.reversed().groupBy { it.dateAdded }.forEach { group ->
            mutableList.add(mapToArticleTitle((group.key.toString()),
                group.value.size))
            group.value.reversed().forEach {
                mutableList.add(it.apply {
                    it.viewType = VIEW_TYPE_HISTORY_NEWS
                })
            }
        }
        return mutableList
    }
}