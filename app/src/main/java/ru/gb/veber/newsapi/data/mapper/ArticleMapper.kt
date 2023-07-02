package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.database.entity.ArticleEntity
import ru.gb.veber.newsapi.data.models.ArticleResponse
import ru.gb.veber.newsapi.domain.models.ArticleModel
import ru.gb.veber.newsapi.domain.models.SourceModel

fun ArticleResponse.toArticleModel(): ArticleModel {
    return ArticleModel(
        id = -1,
        author = author.orEmpty(),
        description = description.orEmpty(),
        publishedAt = publishedAt.orEmpty(),
        publishedAtChange = publishedAt.orEmpty(),
        sourceModel = source?.toSource() ?: SourceModel("-1", ""), //TODO перепровить -1
        title = title.orEmpty(),
        url = url.orEmpty(),
        urlToImage = urlToImage.orEmpty(),
        viewType = 0,
    )
}

fun ArticleEntity.toArticleModel(): ArticleModel {
    return ArticleModel(
        id = id,
        author = author,
        description = description,
        publishedAt = publishedAt,
        publishedAtChange = publishedAt,
        sourceModel = SourceModel(sourceId, sourceName),
        title = title,
        url = url,
        urlToImage = urlToImage,
        isFavorites = isFavorites,
        isHistory = isHistory,
        dateAdded = dateAdded,
        viewType = 0,
    )
}
