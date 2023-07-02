package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.models.SourceResponse
import ru.gb.veber.newsapi.domain.models.SourceModel

fun SourceResponse.toSource(): SourceModel = SourceModel(id = id ?: "-1", name = name)


