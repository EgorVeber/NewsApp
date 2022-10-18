package ru.gb.veber.newsapi.model.repository.room

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.database.dao.AccountSourcesDao
import ru.gb.veber.newsapi.model.database.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.utils.sourcesDbEntityToSources
import ru.gb.veber.newsapi.utils.subscribeDefault

class AccountSourcesRepoImpl(private val accountSourcesDao: AccountSourcesDao) :
    AccountSourcesRepo {
    override fun insert(accountSourcesDbEntity: AccountSourcesDbEntity): Completable {
        return accountSourcesDao.insert(accountSourcesDbEntity).subscribeDefault()
    }

    override fun getLikeSourcesFromAccount(accountId: Int): Single<List<Sources>> {
        return accountSourcesDao.getLikeSourcesFromAccount(accountId).subscribeDefault().map {
            it.map(::sourcesDbEntityToSources)
        }
    }

    override fun deleteSourcesLike(accountId: Int, sourcesId: Int): Completable {
        return accountSourcesDao.deleteSourcesLike(accountId,sourcesId).subscribeDefault()
    }

    override fun deleteSources(accountId: Int): Completable {
        return accountSourcesDao.deleteSources(accountId).subscribeDefault()
    }
}