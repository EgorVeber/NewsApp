package ru.gb.veber.newsapi.model.repository.room

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity

interface AccountRepo {
    fun createAccount(accountDbEntity: AccountDbEntity): Completable
    fun updateAccount(accountDbEntity: AccountDbEntity): Completable
    fun updateAccountById(accountId: Int, saveHistory: Boolean): Completable
    fun deleteAccount(account:Int): Completable
    fun deleteAllAccount(): Completable
    fun getAccountById(accountId: Int): Single<Account>
    fun getAccountByUserName(userName: String): Single<Account>

    suspend fun createAccountV2(accountDbEntity: AccountDbEntity)
    suspend fun updateAccountV2(accountDbEntity: AccountDbEntity)
    suspend fun updateAccountByIdV2(accountId: Int, saveHistory: Boolean)
    suspend fun deleteAccountV2(account:Int)
    suspend fun deleteAllAccountV2()
    suspend fun getAccountByIdV2(accountId: Int): Account
    suspend fun getAccountByUserNameV2(userName: String): Account
}