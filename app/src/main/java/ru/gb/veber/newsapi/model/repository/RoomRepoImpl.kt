package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.database.dao.AccountsDao
import ru.gb.veber.newsapi.model.database.data.Account
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity
import ru.gb.veber.newsapi.utils.mapToAccount
import ru.gb.veber.newsapi.utils.subscribeDefault

class RoomRepoImpl(private val accountDao: AccountsDao) : RoomRepo {

    override fun createAccount(accountDbEntity: AccountDbEntity): Completable {
        return accountDao.createAccount(accountDbEntity).subscribeDefault()
    }

    override fun updateAccount(accountDbEntity: AccountDbEntity): Completable {
        return accountDao.updateAccount(accountDbEntity).subscribeDefault()
    }

    override fun deleteAccount(accountDbEntity: AccountDbEntity): Completable {
        return accountDao.deleteAccount(accountDbEntity)
    }

    override fun getAccountById(accountId: Int): Single<Account> {
        return accountDao.getAccountById(accountId).subscribeDefault().map(::mapToAccount)
    }

    override fun getAccountByUserName(userName: String): Single<Account> {
        return accountDao.getAccountByUserName(userName).subscribeDefault().map(::mapToAccount)
    }
}