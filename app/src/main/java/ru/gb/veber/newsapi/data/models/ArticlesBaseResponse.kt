package ru.gb.veber.newsapi.data.models

class ArticlesBaseResponse(
    val articles: List<ArticleResponse>,
    val status: String,
    val totalResults: Int,
)