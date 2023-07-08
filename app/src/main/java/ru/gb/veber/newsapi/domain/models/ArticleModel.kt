package ru.gb.veber.newsapi.domain.models

import ru.gb.veber.newsapi.common.extentions.DateFormatter.toStringFormatDateDefault
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
    //TODO ViewType не должен утекать в domain
    var isHistory: Boolean = false,
    var isFavorites: Boolean = false,
    var dateAdded: String = Date().toStringFormatDateDefault(),
    var showHistory: Boolean = true,
)