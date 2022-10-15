package ru.gb.veber.newsapi.model.database.dao

import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.database.entity.HistorySelectDbEntity

@Dao
interface HistorySelectDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertSelect(historyDbEntity: HistorySelectDbEntity): Completable

    @Delete
    fun deleteASelect(historyDbEntity: HistorySelectDbEntity): Completable

    @Query("Select * from history_select where account_id =:accountId")
    fun getHistoryById(accountId: Int): Single<List<HistorySelectDbEntity>>
}