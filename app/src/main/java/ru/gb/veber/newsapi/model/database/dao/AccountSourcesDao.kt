package ru.gb.veber.newsapi.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.database.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.model.database.entity.SourcesDbEntity


@Dao
interface AccountSourcesDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(accountSourcesDbEntity: AccountSourcesDbEntity): Completable

    @Query("Select * from sources join account_sources on sources.id = account_sources.sources_id  where account_sources.account_id =:accountId")
    fun getLikeSourcesFromAccount(accountId: Int): Single<List<SourcesDbEntity>>

    @Query("Delete from account_sources where account_id =:accountId and sources_id =:sourcesId ")
    fun deleteSourcesLike(accountId: Int, sourcesId: Int): Completable
}