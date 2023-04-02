package ru.gb.veber.newsapi.data.repository

import ru.gb.veber.newsapi.data.models.room.dao.HistorySelectDao
import ru.gb.veber.newsapi.data.models.room.entity.HistorySelectDbEntity
import ru.gb.veber.newsapi.domain.repository.HistorySelectRepo

class HistorySelectRepoImpl(private val historySelectDao: HistorySelectDao) : HistorySelectRepo {
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
