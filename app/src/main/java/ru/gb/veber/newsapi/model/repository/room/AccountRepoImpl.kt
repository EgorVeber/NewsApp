package ru.gb.veber.newsapi.model.repository.room

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.database.dao.AccountsDao
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity
import ru.gb.veber.newsapi.utils.mapper.toAccount
import ru.gb.veber.newsapi.utils.subscribeDefault

class AccountRepoImpl(private val accountDao: AccountsDao) : AccountRepo {

    override fun createAccount(accountDbEntity: AccountDbEntity): Completable {
        return accountDao.createAccount(accountDbEntity).subscribeDefault()
    }

    override fun updateAccount(accountDbEntity: AccountDbEntity): Completable {
        return accountDao.updateAccount(accountDbEntity).subscribeDefault()
    }

    override fun updateAccountById(accountId: Int, saveHistory: Boolean): Completable {
        return accountDao.updateAccountById(accountId, saveHistory).subscribeDefault()
    }

    override fun deleteAccount(accountID: Int): Completable {
        return accountDao.deleteAccount(accountID).subscribeDefault()
    }

    override fun deleteAllAccount(): Completable {
        return accountDao.deleteAllAccount().subscribeDefault()
    }

    override fun getAccountById(accountId: Int): Single<Account> {
        return accountDao.getAccountById(accountId).subscribeDefault()
            .map { accountDb -> accountDb.toAccount() }
    }

    override fun getAccountByUserName(userName: String): Single<Account> {
        return accountDao.getAccountByUserName(userName).subscribeDefault().map { accountDb->
            accountDb.toAccount()
        }
    }
}