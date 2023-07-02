package ru.gb.veber.newsapi.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.gb.veber.newsapi.data.database.entity.HistorySelectEntity

@Dao
interface HistorySelectDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSelect(historyDbEntity: HistorySelectEntity)

    @Delete
    suspend fun deleteASelect(historyDbEntity: HistorySelectEntity)

    @Query("Select * from history_select where account_id =:accountId")
    suspend fun getHistoryById(accountId: Int): List<HistorySelectEntity>

    @Query("Delete  from history_select where account_id =:accountIdPresenter")
    suspend fun deleteHistoryById(accountIdPresenter: Int)
}
