package ru.gb.veber.newsapi.data.repository

import ru.gb.veber.newsapi.data.database.dao.SourcesDao
import ru.gb.veber.newsapi.data.mapper.toSources
import ru.gb.veber.newsapi.data.mapper.toSourcesEntity
import ru.gb.veber.newsapi.domain.models.SourcesModel
import ru.gb.veber.newsapi.domain.repository.SourcesRepo

class SourcesRepoImpl(private val sourcesDao: SourcesDao) : SourcesRepo {
    override suspend fun insertAll(sourcesModelList: List<SourcesModel>) {
        sourcesDao.insertAll(sourcesModelList.map { sources -> sources.toSourcesEntity() })
    }

    override suspend fun getSources(): List<SourcesModel> =
        sourcesDao.getSources().map { sourcesDb -> sourcesDb.toSources() }
}
