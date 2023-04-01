package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.extentions.FORMAT_DATE
import ru.gb.veber.newsapi.common.extentions.formatDateTime
import ru.gb.veber.newsapi.common.extentions.formatHour
import ru.gb.veber.newsapi.common.extentions.stringFromData
import ru.gb.veber.newsapi.common.extentions.takeDate
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.data.models.network.ArticleDTO
import ru.gb.veber.newsapi.data.models.room.entity.ArticleDbEntity
import ru.gb.veber.newsapi.domain.models.Article
import ru.gb.veber.newsapi.domain.models.Source
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

const val HIDE_HISTORY = "HIDE_HISTORY"

fun Article.toArticleUI(): Article {
    return this.also { article ->
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
    }
}

fun ArticleDTO.toArticle(): Article {
    return Article(
        author = this.author,
        description = this.description,
        publishedAt = this.publishedAt,
        publishedAtChange = this.publishedAt,
        source = this.source.toSource(),
        title = this.title,
        url = this.url,
        urlToImage = this.urlToImage,
    )
}

fun ArticleDbEntity.toArticle(): Article {
    return Article(
        author = this.author,
        description = this.description,
        publishedAt = this.publishedAt,
        publishedAtChange = this.publishedAt,
        source = getNewSource(this.sourceId, this.sourceName),
        title = this.title,
        url = this.url,
        urlToImage = this.urlToImage,
        isFavorites = this.isFavorites,
        isHistory = this.isHistory,
        dateAdded = this.dateAdded
    )
}


fun getNewArticleHistory(groupTitleDate: String, countArticleDate: Int): Article {
    return Article(
        author = countArticleDate.toString(),
        description = "",
        publishedAt = groupTitleDate,
        publishedAtChange = "",
        source = Source("0", ""),
        title = SHOW_HISTORY,
        url = "",
        urlToImage = "",
        dateAdded = "",
        viewType = BaseViewHolder.VIEW_TYPE_HISTORY_HEADER
    )
}

