package ru.gb.veber.newsapi.utils

import ru.gb.veber.newsapi.model.*
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity
import ru.gb.veber.newsapi.model.database.entity.ArticleDbEntity


fun mapToArticle(item: ArticleDTO): Article {
    return Article(
        author = item.author,
        description = item.description,
        publishedAt = item.publishedAt,
        publishedAtChange = item.publishedAt,
        source = mapToSources(item.source),
        title = item.title,
        url = item.url,
        urlToImage = item.urlToImage,
    )
}

fun mapToSources(item: SourceDTO): Source {
    return Source(
        id = item.id,
        name = item.name
    )
}

//ROOM
fun mapToAccount(item: AccountDbEntity): Account {
    return Account(
        id = item.id,
        userName = item.userName,
        email = item.email,
        createdAt = item.createdAt,
        password = item.password
    )
}

fun mapToArticleDbEntity(article: Article, accountId: Int): ArticleDbEntity {
    return ArticleDbEntity(
        id = 0,
        accountID = accountId,
        author = article.author,
        description = article.description,
        publishedAt = article.publishedAt,
        sourceId = article.source.id ?: "none",
        sourceName = article.source.name,
        title = article.title,
        url = article.url,
        urlToImage = article.urlToImage,
        isHistory = true,
        isFavorites = false
    )
}