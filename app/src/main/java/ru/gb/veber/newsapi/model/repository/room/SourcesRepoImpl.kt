package ru.gb.veber.newsapi.model.repository.room

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.database.dao.SourcesDao
import ru.gb.veber.newsapi.model.database.entity.SourcesDbEntity
import ru.gb.veber.newsapi.utils.mapper.toSources
import ru.gb.veber.newsapi.utils.extentions.subscribeDefault

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