package ru.gb.veber.newsapi.model.database.dao

import android.accounts.Account
import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity

@Dao
interface AccountsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun createAccount(accountDbEntity: AccountDbEntity): Completable

    @Update
    fun updateAccount(accountDbEntity: AccountDbEntity): Completable

    @Query("Update accounts set save_history = :saveHistory  where id = :accountId")
    fun updateAccountById(accountId: Int, saveHistory: Boolean): Completable

    @Query("Delete from accounts where id = :accountId")
    fun deleteAccount(accountId: Int): Completable

    @Query("Delete from accounts")
    fun deleteAllAccount(): Completable

    @Query("Select  * from accounts where id =:accountId")
    fun getAccountById(accountId: Int): Single<AccountDbEntity>

    @Query("Select * from accounts where userName =:userName")
    fun getAccountByUserName(userName: String): Single<AccountDbEntity>
}