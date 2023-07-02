package ru.gb.veber.newsapi.data.repository

import ru.gb.veber.newsapi.data.database.dao.HistorySelectDao
import ru.gb.veber.newsapi.data.mapper.toHistorySelect
import ru.gb.veber.newsapi.data.mapper.toHistorySelectEntity
import ru.gb.veber.newsapi.domain.models.HistorySelectModel
import ru.gb.veber.newsapi.domain.repository.HistorySelectRepo

class HistorySelectRepoImpl(private val historySelectDao: HistorySelectDao) : HistorySelectRepo {
    override suspend fun insertSelect(historySelectModel: HistorySelectModel) {
        historySelectDao.insertSelect(historySelectModel.toHistorySelectEntity())
    }

    override suspend fun deleteSelect(historySelectModel: HistorySelectModel) {
        historySelectDao.deleteASelect(historySelectModel.toHistorySelectEntity())
    }

    override suspend fun getHistoryById(accountId: Int): List<HistorySelectModel> =
        historySelectDao.getHistoryById(accountId)
            .map { historySelectEntity -> historySelectEntity.toHistorySelect() }

    override suspend fun deleteSelectById(accountIdPresenter: Int) {
        historySelectDao.deleteHistoryById(accountIdPresenter)
    }
}
