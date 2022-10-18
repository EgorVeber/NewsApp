package ru.gb.veber.newsapi.model.repository.room

import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.database.entity.AccountSourcesDbEntity

interface AccountSourcesRepo {
    fun insert(accountSourcesDbEntity: AccountSourcesDbEntity): Completable
    fun getLikeSourcesFromAccount(accountId: Int): Single<List<Sources>>
    fun deleteSourcesLike(accountId: Int, sourcesId: Int): Completable
    fun deleteSources(accountId: Int): Completable
}