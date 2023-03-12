package ru.gb.veber.newsapi.data.models.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.data.models.room.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.SourcesDbEntity


@Dao
interface AccountSourcesDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(accountSourcesDbEntity: AccountSourcesDbEntity): Completable

    @Query("Select * from sources join account_sources on sources.id = account_sources.sources_id  where account_sources.account_id =:accountId")
    fun getLikeSourcesFromAccount(accountId: Int): Single<List<SourcesDbEntity>>

    @Query("Delete from account_sources where account_id =:accountId and sources_id =:sourcesId ")
    fun deleteSourcesLike(accountId: Int, sourcesId: Int): Completable

    @Query("Delete from account_sources where account_id =:accountId ")
    fun deleteSources(accountId: Int): Completable

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertV2(accountSourcesDbEntity: AccountSourcesDbEntity)

    @Query("Select * from sources join account_sources on sources.id = account_sources.sources_id  where account_sources.account_id =:accountId")
    suspend fun getLikeSourcesFromAccountV2(accountId: Int): List<SourcesDbEntity>

    @Query("Delete from account_sources where account_id =:accountId and sources_id =:sourcesId ")
    suspend fun deleteSourcesLikeV2(accountId: Int, sourcesId: Int)

    @Query("Delete from account_sources where account_id =:accountId ")
    suspend fun deleteSourcesV2(accountId: Int)
}
