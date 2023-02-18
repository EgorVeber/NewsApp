package ru.gb.veber.newsapi.utils.mapper

import ru.gb.veber.newsapi.model.database.entity.SourcesDbEntity
import ru.gb.veber.newsapi.model.network.SourcesDTO

fun SourcesDTO.toSourcesDbEntity(): SourcesDbEntity {
    return SourcesDbEntity(
        id = 0,
        idSources = this.id,
        name = this.name,
        description = this.description,
        url = this.url,
        category = this.category,
        language = this.language,
        country = this.country
    )
}