package ru.gb.veber.newsapi.data.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.common.extentions.subscribeDefault
import ru.gb.veber.newsapi.data.mapper.toSources
import ru.gb.veber.newsapi.data.models.room.dao.SourcesDao
import ru.gb.veber.newsapi.data.models.room.entity.SourcesDbEntity
import ru.gb.veber.newsapi.domain.models.Sources
import ru.gb.veber.newsapi.domain.repository.SourcesRepo

class SourcesRepoImpl(private val sourcesDao: SourcesDao) : SourcesRepo {
    override fun insertAll(sourcesDbEntity: List<SourcesDbEntity>): Completable {
        return sourcesDao.insertAll(sourcesDbEntity).subscribeDefault()
    }

    override fun getSources(): Single<MutableList<Sources>> {
        return sourcesDao.getSources().subscribeDefault().map { list ->
            list.map { sourcesDb ->
                sourcesDb.toSources()
            }.toMutableList()
        }
    }

    override suspend fun insertAllV2(sourcesDbEntity: List<SourcesDbEntity>) {
        sourcesDao.insertAllV2(sourcesDbEntity)
    }

    override suspend fun getSourcesV2(): MutableList<Sources> {
        return sourcesDao.getSourcesV2().map { sourcesDb ->
            sourcesDb.toSources()
        }.toMutableList()
    }
}
