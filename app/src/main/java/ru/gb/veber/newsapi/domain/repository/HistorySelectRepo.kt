package ru.gb.veber.newsapi.domain.repository


import ru.gb.veber.newsapi.data.models.room.entity.HistorySelectDbEntity

interface HistorySelectRepo {
    suspend fun insertSelectV2(historyDbEntity: HistorySelectDbEntity)
    suspend fun deleteSelectV2(historyDbEntity: HistorySelectDbEntity)
    suspend fun getHistoryByIdV2(accountId: Int): List<HistorySelectDbEntity>
    suspend fun deleteSelectByIdV2(accountIdPresenter: Int)
}
