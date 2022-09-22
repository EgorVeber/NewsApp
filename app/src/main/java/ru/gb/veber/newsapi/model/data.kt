package ru.gb.veber.newsapi.model

data class Article(
    var author: String?,
    var description: String?,
    var publishedAt: String,
    var publishedAtChange: String?,
    var source: Source,
    var title: String,
    var url: String,
    var urlToImage: String?,
    var viewType: Int=0,
)

data class Source(
    var id: String?,
    var name: String,
)
