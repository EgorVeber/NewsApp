package ru.gb.veber.newsapi.model.repository

import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.database.data.Account
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity

interface RoomRepo {
    fun createAccount(accountDbEntity: AccountDbEntity): Completable
    fun updateAccount(accountDbEntity: AccountDbEntity): Completable
    fun deleteAccount(accountDbEntity: AccountDbEntity): Completable
    fun getAccountById(accountId: Int): Single<Account>
    fun getAccountByUserName(userName: String): Single<Account>
}