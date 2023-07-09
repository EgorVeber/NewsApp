package ru.gb.veber.newsapi.presentation.models

import ru.gb.veber.newsapi.domain.models.SourceModel

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
    var viewType: Int = 0,
    var isHistory: Boolean = false,
    var isFavorites: Boolean = false,
    var dateAdded: String,
    var showHistory: Boolean = true,
)