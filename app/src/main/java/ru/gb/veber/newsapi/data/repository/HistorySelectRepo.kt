package ru.gb.veber.newsapi.data.repository


import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.data.models.room.entity.HistorySelectDbEntity

interface HistorySelectRepo {
    fun insertSelect(historyDbEntity: HistorySelectDbEntity): Completable
    fun deleteSelect(historyDbEntity: HistorySelectDbEntity): Completable
    fun getHistoryById(accountId: Int): Single<List<HistorySelectDbEntity>>
    fun deleteSelectById(accountIdPresenter: Int): Completable

    suspend fun insertSelectV2(historyDbEntity: HistorySelectDbEntity)
    suspend fun deleteSelectV2(historyDbEntity: HistorySelectDbEntity)
    suspend fun getHistoryByIdV2(accountId: Int): List<HistorySelectDbEntity>
    suspend fun deleteSelectByIdV2(accountIdPresenter: Int)
}
