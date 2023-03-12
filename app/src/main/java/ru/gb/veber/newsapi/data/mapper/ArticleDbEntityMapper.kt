package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.data.models.room.entity.ArticleDbEntity

fun Article.toArticleDbEntity(accountId: Int): ArticleDbEntity {
    return ArticleDbEntity(
        id = 0,
        accountID = accountId,
        author = this.author,
        description = this.description,
        publishedAt = this.publishedAt,
        sourceId = this.source.id ?: "",
        sourceName = this.source.name,
        title = this.title.toString(),
        url = this.url,
        urlToImage = this.urlToImage,
        isHistory = this.isHistory,
        isFavorites = this.isFavorites,
        dateAdded = this.dateAdded.toString()
    )
}
