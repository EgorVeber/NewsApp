package ru.gb.veber.newsapi.domain.models

import ru.gb.veber.ui_common.utils.DateFormatter.toStringFormatDateDefault
import java.util.Date

data class ArticleModel(
    var id: Int,// TODO понять нужно или нект
    var author: String,
    var description: String,
    var publishedAt: String,
    var sourceModel: SourceModel,
    var title: String,
    var url: String,
    var urlToImage: String,
    var isHistory: Boolean = false,
    var isFavorites: Boolean = false,
    // TODO Убрать из домена сеттить в Ui
    var dateAdded: String = Date().toStringFormatDateDefault(),
    var showHistory: Boolean = true,
)