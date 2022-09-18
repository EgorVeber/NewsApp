package ru.gb.veber.newsapi.utils

import ru.gb.veber.newsapi.model.*


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