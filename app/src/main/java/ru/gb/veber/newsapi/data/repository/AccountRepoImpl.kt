package ru.gb.veber.newsapi.data.repository

import ru.gb.veber.newsapi.data.database.dao.AccountsDao
import ru.gb.veber.newsapi.data.mapper.toAccount
import ru.gb.veber.newsapi.data.mapper.toAccountDbEntity
import ru.gb.veber.newsapi.domain.models.AccountModel
import ru.gb.veber.newsapi.domain.repository.AccountRepo

class AccountRepoImpl(private val accountDao: AccountsDao) : AccountRepo {
    override suspend fun createAccount(accountModel: AccountModel) {
        accountDao.createAccount(accountModel.toAccountDbEntity())
    }

    override suspend fun updateAccount(accountModel: AccountModel) {
        accountDao.updateAccount(accountModel.toAccountDbEntity())
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

    override suspend fun getAccountByIdV2(accountId: Int): AccountModel {
        return accountDao.getAccountByIdV2(accountId).toAccount()
    }

    override suspend fun getAccountByUserNameV2(userName: String): AccountModel {
        return accountDao.getAccountByUserNameV2(userName).toAccount()
    }
}
