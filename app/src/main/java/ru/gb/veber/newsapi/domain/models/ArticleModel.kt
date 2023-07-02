package ru.gb.veber.newsapi.domain.models

import ru.gb.veber.newsapi.common.extentions.DateFormatter.toStringFormatDateUi
import java.util.Date

data class ArticleModel(
    var id: Int,
    var author: String,
    var description: String,
    var publishedAt: String,
    var publishedAtChange: String,
    var sourceModel: SourceModel,
    var title: String,
    var url: String,
    var urlToImage: String,
    //TODO ViewType не должен утекать в domain
    var viewType: Int,
    var isHistory: Boolean = false,
    var isFavorites: Boolean = false,
    var dateAdded: String = Date().toStringFormatDateUi(),
    var showHistory: Boolean = true,
)