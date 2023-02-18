package ru.gb.veber.newsapi.utils.mapper

import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.database.entity.SourcesDbEntity

fun SourcesDbEntity.toSources(): Sources {
    return Sources(
        id = this.id,
        idSources = this.idSources,
        name = this.name,
        description = this.description,
        url = this.url,
        category = this.category,
        language = this.language,
        country = this.country
    )
}