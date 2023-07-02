package ru.gb.veber.newsapi.domain.repository

import ru.gb.veber.newsapi.domain.models.AccountModel

interface AccountRepo {
    suspend fun createAccount(accountModel: AccountModel)
    suspend fun updateAccount(accountModel: AccountModel)
    suspend fun updateAccountByIdV2(accountId: Int, saveHistory: Boolean)
    suspend fun deleteAccountV2(account:Int)
    suspend fun deleteAllAccountV2()
    suspend fun getAccountByIdV2(accountId: Int): AccountModel
    suspend fun getAccountByUserNameV2(userName: String): AccountModel
}
