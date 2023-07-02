package ru.gb.veber.newsapi.domain.models

data class ArticlesBaseModel(
    val articles: List<ArticleModel>,
    val status: String,
    val totalResults: Int,
)