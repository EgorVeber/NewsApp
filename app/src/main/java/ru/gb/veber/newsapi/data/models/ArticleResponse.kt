package ru.gb.veber.newsapi.data.models

class ArticleResponse(
    var author: String? = null,
    val content: String? = null,
    val description: String? = null,
    var publishedAt: String? = null,
    val source: SourceResponse? = null,
    val title: String? = null,
    val url: String? = null,
    val urlToImage: String?,
)
