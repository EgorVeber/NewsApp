package ru.gb.veber.newsapi.model

data class Article(
    var author: String?,
    var description: String?,
    var publishedAt: String,
    var source: Source,
    var title: String,
    var url: String,
    var urlToImage: String?,
)

data class Source(
    var id: String?,
    var name: String,
)
