package ru.gb.veber.newsapi.model.repository.room

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.database.dao.AccountSourcesDao
import ru.gb.veber.newsapi.model.database.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.utils.mapper.toSources
import ru.gb.veber.newsapi.utils.extentions.subscribeDefault

class AccountSourcesRepoImpl(private val accountSourcesDao: AccountSourcesDao) :
    AccountSourcesRepo {
    override fun insert(accountSourcesDbEntity: AccountSourcesDbEntity): Completable {
        return accountSourcesDao.insert(accountSourcesDbEntity).subscribeDefault()
    }

    override fun getLikeSourcesFromAccount(accountId: Int): Single<List<Sources>> {
        return accountSourcesDao.getLikeSourcesFromAccount(accountId).subscribeDefault()
            .map { list ->
                list.map { sourcesDb ->
                    sourcesDb.toSources()
                }
            }
    }

    override fun deleteSourcesLike(accountId: Int, sourcesId: Int): Completable {
        return accountSourcesDao.deleteSourcesLike(accountId, sourcesId).subscribeDefault()
    }

    override fun deleteSources(accountId: Int): Completable {
        return accountSourcesDao.deleteSources(accountId).subscribeDefault()
    }


    override suspend fun insertV2(accountSourcesDbEntity: AccountSourcesDbEntity) {
        accountSourcesDao.insertV2(accountSourcesDbEntity)
    }

    override suspend fun getLikeSourcesFromAccountV2(accountId: Int): List<Sources> {
        return accountSourcesDao.getLikeSourcesFromAccountV2(accountId).map { sourcesDb ->
            sourcesDb.toSources()
        }
    }

    override suspend fun deleteSourcesLikeV2(accountId: Int, sourcesId: Int) {
        accountSourcesDao.deleteSourcesLikeV2(accountId, sourcesId)
    }

    override suspend fun deleteSourcesV2(accountId: Int) {
        accountSourcesDao.deleteSourcesV2(accountId)
    }
}