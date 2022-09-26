package ru.gb.veber.newsapi.utils

import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.ArticleDTO
import ru.gb.veber.newsapi.model.Source
import ru.gb.veber.newsapi.model.SourceDTO
import ru.gb.veber.newsapi.model.database.data.Account
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity


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
        createdAt = item.createdAt
    )
}