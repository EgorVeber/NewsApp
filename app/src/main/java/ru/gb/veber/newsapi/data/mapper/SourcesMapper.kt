package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.models.room.entity.SourcesDbEntity
import ru.gb.veber.newsapi.domain.models.Sources

fun SourcesDbEntity.toSources(): Sources =
    Sources(
        id = id,
        idSources = idSources,
        name = name,
        description = description,
        url = url,
        category = category,
        language = language,
        country = country
    )
