package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.models.network.SourcesDTO
import ru.gb.veber.newsapi.data.models.room.entity.SourcesDbEntity

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
