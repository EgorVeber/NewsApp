package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.database.entity.SourcesEntity
import ru.gb.veber.newsapi.domain.models.SourcesModel

fun SourcesModel.toSourcesEntity(): SourcesEntity {
    return SourcesEntity(
        id = 0,
        idSources = idSources,
        name = name,
        description = description,
        url = url,
        category = category,
        language = language,
        country = country
    )
}
