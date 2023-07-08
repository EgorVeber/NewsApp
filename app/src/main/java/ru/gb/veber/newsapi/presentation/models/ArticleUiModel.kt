package ru.gb.veber.newsapi.presentation.models

import ru.gb.veber.newsapi.common.extentions.DateFormatter.toStringFormatDateDefault
import ru.gb.veber.newsapi.common.extentions.DateFormatter.toStringFormatDateYearMonthDay
import ru.gb.veber.newsapi.domain.models.SourceModel
import java.util.Date

data class ArticleUiModel(
    var id: Int,
    var author: String,
    var description: String,
    var publishedAt: String,
    var publishedAtUi: String,
    var sourceModel: SourceModel,
    var title: String,
    var url: String,
    var urlToImage: String,
    //TODO ViewType не должен утекать в domain
    var viewType: Int = 0,
    var isHistory: Boolean = false,
    var isFavorites: Boolean = false,
    var dateAdded: String = Date().toStringFormatDateYearMonthDay(),
    var showHistory: Boolean = true,
)