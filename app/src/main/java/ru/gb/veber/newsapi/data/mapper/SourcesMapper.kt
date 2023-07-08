package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.database.entity.SourcesEntity
import ru.gb.veber.newsapi.data.models.SourcesResponse
import ru.gb.veber.newsapi.domain.models.SourcesModel

fun SourcesEntity.toSources(): SourcesModel =
    SourcesModel(
        id = id,
        idSources = idSources,
        name = name,
        description = description,
        url = url,
        category = category,
        language = language,
        country = country
    )

fun SourcesResponse.toSources(): SourcesModel =
    SourcesModel(
        id = 0,
        idSources = id,
        name = name,
        description = description,
        url = url,
        category = category,
        language = language,
        country = country
    )

