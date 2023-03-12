package ru.gb.veber.newsapi.data.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.data.models.room.entity.AccountSourcesDbEntity

interface AccountSourcesRepo {
    fun insert(accountSourcesDbEntity: AccountSourcesDbEntity): Completable
    fun getLikeSourcesFromAccount(accountId: Int): Single<List<Sources>>
    fun deleteSourcesLike(accountId: Int, sourcesId: Int): Completable
    fun deleteSources(accountId: Int): Completable

    suspend fun insertV2(accountSourcesDbEntity: AccountSourcesDbEntity)
    suspend fun getLikeSourcesFromAccountV2(accountId: Int): List<Sources>
    suspend fun deleteSourcesLikeV2(accountId: Int, sourcesId: Int)
    suspend fun deleteSourcesV2(accountId: Int)
}
