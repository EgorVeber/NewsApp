package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.models.SourcesBaseResponse
import ru.gb.veber.newsapi.domain.models.SourcesBaseModel

fun SourcesBaseResponse.toSourcesBaseModel() = SourcesBaseModel(
    status = status,
    sources = sources.map { source -> source.toSources() }
)