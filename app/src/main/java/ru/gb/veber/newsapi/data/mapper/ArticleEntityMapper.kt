package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.database.entity.ArticleEntity
import ru.gb.veber.newsapi.domain.models.ArticleModel

fun ArticleModel.toArticleDbEntity(accountId: Int): ArticleEntity {
    return ArticleEntity(
        id = 0,
        accountID = accountId,
        author = author,
        description = description,
        publishedAt = publishedAt,
        sourceId = sourceModel.id ,
        sourceName = sourceModel.name,
        title = title,
        url = url,
        urlToImage = urlToImage,
        isHistory = isHistory,
        isFavorites = isFavorites,
        dateAdded = dateAdded
    )
}
