package ru.gb.veber.newsapi.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.gb.veber.newsapi.data.database.entity.AccountSourcesEntity
import ru.gb.veber.newsapi.data.database.entity.SourcesEntity


@Dao
interface AccountSourcesDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(accountSourcesEntity: AccountSourcesEntity)

    @Query("Select * from sources join account_sources on sources.id = account_sources.sources_id  where account_sources.account_id =:accountId")
    suspend fun getLikeSourcesFromAccount(accountId: Int): List<SourcesEntity>

    @Query("Delete from account_sources where account_id =:accountId and sources_id =:sourcesId ")
    suspend fun deleteSourcesLikeV2(accountId: Int, sourcesId: Int)

    @Query("Delete from account_sources where account_id =:accountId ")
    suspend fun deleteSourcesV2(accountId: Int)
}
