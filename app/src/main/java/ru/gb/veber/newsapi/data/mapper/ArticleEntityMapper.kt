package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.database.entity.ArticleEntity
import ru.gb.veber.newsapi.domain.models.ArticleModel

fun ArticleModel.toArticleDbEntity(accountId: Int): ArticleEntity {
    return ArticleEntity(
        id = 0,
        accountID = accountId,
        author = this.author,
        description = this.description,
        publishedAt = this.publishedAt,
        sourceId = this.sourceModel.id ,
        sourceName = this.sourceModel.name,
        title = this.title,
        url = this.url,
        urlToImage = this.urlToImage,
        isHistory = this.isHistory,
        isFavorites = this.isFavorites,
        dateAdded = this.dateAdded
    )
}
