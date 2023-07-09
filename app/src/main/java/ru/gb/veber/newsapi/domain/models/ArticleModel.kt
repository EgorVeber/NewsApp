package ru.gb.veber.newsapi.domain.models

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
    var dateAdded: String,
    var showHistory: Boolean = true,
)