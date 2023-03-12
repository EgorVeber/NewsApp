package ru.gb.veber.newsapi.data.models.room.dao

import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.data.models.room.entity.HistorySelectDbEntity

@Dao
interface HistorySelectDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertSelect(historyDbEntity: HistorySelectDbEntity): Completable

    @Delete
    fun deleteASelect(historyDbEntity: HistorySelectDbEntity): Completable

    @Query("Select * from history_select where account_id =:accountId")
    fun getHistoryById(accountId: Int): Single<List<HistorySelectDbEntity>>

    @Query("Delete  from history_select where account_id =:accountIdPresenter")
    fun deleteHistoryById(accountIdPresenter: Int): Completable

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSelectV2(historyDbEntity: HistorySelectDbEntity)

    @Delete
    suspend fun deleteASelectV2(historyDbEntity: HistorySelectDbEntity)

    @Query("Select * from history_select where account_id =:accountId")
    suspend fun getHistoryByIdV2(accountId: Int): List<HistorySelectDbEntity>

    @Query("Delete  from history_select where account_id =:accountIdPresenter")
    suspend fun deleteHistoryByIdV2(accountIdPresenter: Int)
}
