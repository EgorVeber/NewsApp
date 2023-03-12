package ru.gb.veber.newsapi.data.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.data.models.room.dao.AccountsDao
import ru.gb.veber.newsapi.data.models.room.entity.AccountDbEntity
import ru.gb.veber.newsapi.data.mapper.toAccount
import ru.gb.veber.newsapi.core.utils.extentions.subscribeDefault

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
        return accountDao.getAccountByUserName(userName).subscribeDefault().map { accountDb ->
            accountDb.toAccount()
        }
    }

    override suspend fun createAccountV2(accountDbEntity: AccountDbEntity) {
        accountDao.createAccountV2(accountDbEntity)
    }

    override suspend fun updateAccountV2(accountDbEntity: AccountDbEntity) {
        accountDao.updateAccountV2(accountDbEntity)
    }

    override suspend fun updateAccountByIdV2(accountId: Int, saveHistory: Boolean) {
        accountDao.updateAccountByIdV2(accountId, saveHistory)
    }

    override suspend fun deleteAccountV2(accountID: Int) {
        accountDao.deleteAccountV2(accountID)
    }

    override suspend fun deleteAllAccountV2() {
        accountDao.deleteAllAccountV2()
    }

    override suspend fun getAccountByIdV2(accountId: Int): Account {
        return accountDao.getAccountByIdV2(accountId).toAccount()
    }

    override suspend fun getAccountByUserNameV2(userName: String): Account {
        return accountDao.getAccountByUserNameV2(userName).toAccount()
    }
}
