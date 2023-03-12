package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.data.models.room.entity.SourcesDbEntity

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
