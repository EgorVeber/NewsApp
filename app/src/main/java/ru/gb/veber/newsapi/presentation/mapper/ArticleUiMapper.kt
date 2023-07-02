package ru.gb.veber.newsapi.presentation.mapper

import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.extentions.DateFormatter.FORMAT_DATE
import ru.gb.veber.newsapi.common.extentions.DateFormatter.toStringFormatDateUi
import ru.gb.veber.newsapi.common.extentions.DateFormatter.formatHour
import ru.gb.veber.newsapi.common.extentions.DateFormatter.takeDate
import ru.gb.veber.newsapi.common.extentions.DateFormatter.toDateFormatDateServerResponse
import ru.gb.veber.newsapi.common.extentions.DateFormatter.toFormatDateUi
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.domain.models.ArticleModel
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun ArticleUiModel.toArticleModel(): ArticleModel =
    ArticleModel(
        id = id,
        author = author,
        description = description,
        publishedAt = publishedAt,
        publishedAtChange = publishedAtUi,
        sourceModel = sourceModel,
        title = title,
        url = url,
        urlToImage = urlToImage,
        viewType = viewType,
        isHistory = isHistory,
        isFavorites = isFavorites,
        dateAdded = dateAdded,
        showHistory = showHistory,
    )

fun ArticleModel.toArticleUiModel(changeDateFormat: Boolean = false): ArticleUiModel {
    //TODO Подумать про стринг провайдер или что то со строками придумать константы ?
    val anonymousOnStr = App.instance.applicationContext.getString(R.string.anonymous_author)
    val readOnStr = App.instance.applicationContext.getString(R.string.readOn)
    return ArticleUiModel(
        id = id,
        author = author.ifEmpty { anonymousOnStr },
        description = if (description.isEmpty()) "$readOnStr ${sourceModel.name}" else "$description.",
        publishedAt = publishedAt,
        publishedAtUi = if (changeDateFormat) publishedAtChange else getPublishDate(publishedAt.toFormatDateUi()),
        sourceModel = sourceModel,
        title = title.ifEmpty { publishedAt },
        url = url,
        urlToImage = urlToImage,
        viewType = viewType,
        isHistory = isHistory
    )
}

private fun getPublishDate(publishedAt: String): String {
    //TODO доделать с датой
    val publishedDate: Date = publishedAt.toDateFormatDateServerResponse()
    val simpleFormat: SimpleDateFormat = SimpleDateFormat(FORMAT_DATE, Locale.getDefault())
    val todayStr = App.instance.applicationContext.getString(R.string.today)
    val yesterdayStr = App.instance.applicationContext.getString(R.string.yesterday)

    return if (simpleFormat.format(publishedDate).equals(simpleFormat.format(Date()))) {
        "$todayStr ${publishedDate.formatHour()}"
    } else if (simpleFormat.format(publishedDate).equals(simpleFormat.format(takeDate(-1)))) {
        "$yesterdayStr ${publishedDate.formatHour()}"
    } else {
        publishedDate.toStringFormatDateUi()
    }
}