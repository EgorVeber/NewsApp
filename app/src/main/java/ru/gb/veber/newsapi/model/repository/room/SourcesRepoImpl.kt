package ru.gb.veber.newsapi.model.repository.room

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.database.dao.ArticleDao
import ru.gb.veber.newsapi.model.database.dao.SourcesDao
import ru.gb.veber.newsapi.model.database.entity.ArticleDbEntity
import ru.gb.veber.newsapi.model.database.entity.SourcesDbEntity
import ru.gb.veber.newsapi.utils.subscribeDefault

class SourcesRepoImpl(private val sourcesDao: SourcesDao) : SourcesRepo {
    override fun insertAll(sourcesDbEntity: List<SourcesDbEntity>): Completable {
        return sourcesDao.insertAll(sourcesDbEntity).subscribeDefault()
    }

    override fun getSources(): Single<List<SourcesDbEntity>> {
        return sourcesDao.getSources().subscribeDefault()
    }
}