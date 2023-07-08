package ru.gb.veber.newsapi.domain.repository


import ru.gb.veber.newsapi.domain.models.SourcesModel

interface SourcesRepo {
    suspend fun insertAll(sourcesModelList: List<SourcesModel>)
    suspend fun getSources(): List<SourcesModel>
}
