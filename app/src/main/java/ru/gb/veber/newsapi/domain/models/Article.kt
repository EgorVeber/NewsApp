package ru.gb.veber.newsapi.domain.models

import ru.gb.veber.newsapi.common.extentions.formatDateTime
import java.util.*

data class Article(
    var id: Int? = 0,
    var author: String?,
    var description: String?,
    var publishedAt: String,
    var publishedAtChange: String?,
    var source: Source,
    var title: String?,
    var url: String,
    var urlToImage: String?,
    var viewType: Int = 0,
    var isHistory: Boolean = false,
    var isFavorites: Boolean = false,
    var dateAdded: String? = Date().formatDateTime(),
    var showHistory: Boolean = true,
)