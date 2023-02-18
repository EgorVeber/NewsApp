package ru.gb.veber.newsapi.utils.mapper

import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.database.entity.ArticleDbEntity

fun mapToArticleDbEntity(article: Article, accountId: Int): ArticleDbEntity {
    return ArticleDbEntity(
        id = 0,
        accountID = accountId,
        author = article.author,
        description = article.description,
        publishedAt = article.publishedAt,
        sourceId = article.source.id ?: "",
        sourceName = article.source.name,
        title = article.title.toString(),
        url = article.url,
        urlToImage = article.urlToImage,
        isHistory = article.isHistory,
        isFavorites = article.isFavorites,
        dateAdded = article.dateAdded.toString()
    )
}
