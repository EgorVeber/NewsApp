package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.models.ArticlesBaseResponse
import ru.gb.veber.newsapi.domain.models.ArticlesBaseModel

fun ArticlesBaseResponse.toArticleBaseModel(): ArticlesBaseModel =
    ArticlesBaseModel(
        articles = articles.map { article -> article.toArticleModel() },
        status = status,
        totalResults = totalResults
    )



