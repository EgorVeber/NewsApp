package ru.gb.veber.newsapi.domain.repository


import ru.gb.veber.newsapi.domain.models.HistorySelectModel

interface HistorySelectRepo {
    suspend fun insertSelect(historyDbEntity: HistorySelectModel)
    suspend fun deleteSelect(historyDbEntity: HistorySelectModel)
    suspend fun getHistoryById(accountId: Int): List<HistorySelectModel>
    suspend fun deleteSelectById(accountIdPresenter: Int)
}
