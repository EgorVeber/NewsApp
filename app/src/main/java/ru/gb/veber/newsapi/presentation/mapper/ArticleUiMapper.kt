package ru.gb.veber.newsapi.presentation.mapper

import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.extentions.DateFormatter.equalsByFormat
import ru.gb.veber.newsapi.common.extentions.DateFormatter.getCurrentDateMinusDay
import ru.gb.veber.newsapi.common.extentions.DateFormatter.toDateFormatDateServerResponse
import ru.gb.veber.newsapi.common.extentions.DateFormatter.toFormatDateDefault
import ru.gb.veber.newsapi.common.extentions.DateFormatter.toStringFormatDateHourMinutes
import ru.gb.veber.newsapi.common.extentions.DateFormatter.toStringFormatDateDefault
import ru.gb.veber.newsapi.common.extentions.getStringByResId
import ru.gb.veber.newsapi.domain.models.ArticleModel
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder
import java.util.Date

fun ArticleUiModel.toArticleModel(): ArticleModel =
    ArticleModel(
        id = id,
        author = author,
        description = description,
        publishedAt = publishedAt,
        sourceModel = sourceModel,
        title = title,
        url = url,
        urlToImage = urlToImage,
        isHistory = isHistory,
        isFavorites = isFavorites,
        dateAdded = dateAdded,
        showHistory = showHistory,
    )

fun ArticleModel.toArticleUiModel(
    changeDateFormat: Boolean = false,
    viewType: Int = BaseViewHolder.VIEW_TYPE_TOP_NEWS,
): ArticleUiModel {
    //TODO Подумать про стринг провайдер или что то со строками придумать константы ?
    val anonymousOnStr = getStringByResId(R.string.anonymous_author)
    val readOnStr = getStringByResId(R.string.readOn)

    return ArticleUiModel(
        id = id,
        author = author.ifEmpty { anonymousOnStr },
        description = if (description.isEmpty()) "$readOnStr ${sourceModel.name}" else "$description.",
        publishedAt = publishedAt.toFormatDateDefault(),
        publishedAtUi = if (changeDateFormat) publishedAt else getDayPublishing(publishedAt),
        sourceModel = sourceModel,
        title = title.ifEmpty { publishedAt },
        url = url,
        urlToImage = urlToImage,
        viewType = viewType,
        isHistory = isHistory
    )
}

private fun getDayPublishing(publishedAt: String): String {
    val todayStr = getStringByResId(R.string.today)
    val yesterdayStr = getStringByResId(R.string.yesterday)

    val publishedDate: Date = publishedAt.toDateFormatDateServerResponse()

    return if (publishedDate.equalsByFormat(Date())) {
        "$todayStr ${publishedDate.toStringFormatDateHourMinutes()}"
    } else if (publishedDate.equalsByFormat(getCurrentDateMinusDay(1))) {
        "$yesterdayStr ${publishedDate.toStringFormatDateHourMinutes()}"
    } else {
        publishedDate.toStringFormatDateDefault()
    }
}

////От строки к дате
//fun Test1() {
//    val dtStart = "2010-10-15T09:27:37Z"
//    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//    try {
//        val date = format.parse(dtStart)
//        println(date)
//    } catch (e: ParseException) {
//        e.printStackTrace()
//    }
//}
//
////От даты к строке
//fun Test() {
//    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//    try {
//        val date = Date()
//        val dateTime = dateFormat.format(date)
//        println("Current Date Time : $dateTime")
//    } catch (e: ParseException) {
//        e.printStackTrace()
//    }
//}
