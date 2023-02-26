package ru.gb.veber.newsapi.model.repository.room

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.database.dao.HistorySelectDao
import ru.gb.veber.newsapi.model.database.entity.HistorySelectDbEntity
import ru.gb.veber.newsapi.utils.extentions.subscribeDefault

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

    override fun deleteSelectById(accountIdPresenter: Int): Completable {
        return historySelectDao.deleteHistoryById(accountIdPresenter).subscribeDefault()
    }

    override suspend fun insertSelectV2(historyDbEntity: HistorySelectDbEntity) {
        historySelectDao.insertSelectV2(historyDbEntity)
    }

    override suspend fun deleteSelectV2(historyDbEntity: HistorySelectDbEntity) {
        historySelectDao.deleteASelectV2(historyDbEntity)
    }

    override suspend fun getHistoryByIdV2(accountId: Int): List<HistorySelectDbEntity> {
        return historySelectDao.getHistoryByIdV2(accountId)
    }

    override suspend fun deleteSelectByIdV2(accountIdPresenter: Int) {
        historySelectDao.deleteHistoryByIdV2(accountIdPresenter)
    }
}