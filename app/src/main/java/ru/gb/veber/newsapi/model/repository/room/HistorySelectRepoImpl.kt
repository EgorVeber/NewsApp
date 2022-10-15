package ru.gb.veber.newsapi.model.repository.room

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.database.dao.HistorySelectDao
import ru.gb.veber.newsapi.model.database.dao.SourcesDao
import ru.gb.veber.newsapi.model.database.entity.HistorySelectDbEntity
import ru.gb.veber.newsapi.model.database.entity.SourcesDbEntity
import ru.gb.veber.newsapi.utils.sourcesDbEntityToSources
import ru.gb.veber.newsapi.utils.subscribeDefault

class HistorySelectRepoImpl(private val historySelectDao: HistorySelectDao) : HistorySelectRepo {
    override fun insertSelect(historyDbEntity: HistorySelectDbEntity): Completable {
        return historySelectDao.insertSelect(historyDbEntity).subscribeDefault()
    }

    override fun deleteSelect(historyDbEntity: HistorySelectDbEntity): Completable {
        return historySelectDao.deleteASelect(historyDbEntity).subscribeDefault()
    }

    override fun getHistoryById(accountId: Int): Single<List<HistorySelectDbEntity>> {
        return historySelectDao.getHistoryById(accountId).subscribeDefault()
    }
}