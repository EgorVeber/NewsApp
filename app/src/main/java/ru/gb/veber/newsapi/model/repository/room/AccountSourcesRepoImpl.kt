package ru.gb.veber.newsapi.model.repository.room

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.database.dao.AccountSourcesDao
import ru.gb.veber.newsapi.model.database.dao.AccountsDao
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity
import ru.gb.veber.newsapi.model.database.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.model.database.entity.SourcesDbEntity
import ru.gb.veber.newsapi.utils.mapToAccount
import ru.gb.veber.newsapi.utils.subscribeDefault

class AccountSourcesRepoImpl(private val accountSourcesDao: AccountSourcesDao) :
    AccountSourcesRepo {
    override fun insert(accountSourcesDbEntity: AccountSourcesDbEntity): Completable {
        return accountSourcesDao.insert(accountSourcesDbEntity).subscribeDefault()
    }

    override fun getLikeSourcesFromAccount(accountId: Int): Single<List<SourcesDbEntity>> {
        return accountSourcesDao.getLikeSourcesFromAccount(accountId).subscribeDefault()
    }

    override fun deleteSourcesLike(accountId: Int, sourcesId: Int): Completable {
        return accountSourcesDao.deleteSourcesLike(accountId,sourcesId).subscribeDefault()
    }


//    override fun getLikeSourcesFromAccount(accountId: Int): Single<Map<SourcesDbEntity, AccountSourcesDbEntity>> {
//        return accountSourcesDao.getLikeSourcesFromAccount(accountId)
//            .subscribeDefault()
//    }
}