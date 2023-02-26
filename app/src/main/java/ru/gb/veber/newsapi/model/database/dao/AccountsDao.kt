package ru.gb.veber.newsapi.model.database.dao

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

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun createAccountV2(accountDbEntity: AccountDbEntity)

    @Update
    suspend fun updateAccountV2(accountDbEntity: AccountDbEntity)

    @Query("Update accounts set save_history = :saveHistory  where id = :accountId")
    suspend fun updateAccountByIdV2(accountId: Int, saveHistory: Boolean)

    @Query("Delete from accounts where id = :accountId")
    suspend fun deleteAccountV2(accountId: Int)

    @Query("Delete from accounts")
    suspend fun deleteAllAccountV2()

    @Query("Select  * from accounts where id =:accountId")
    suspend fun getAccountByIdV2(accountId: Int): AccountDbEntity

    @Query("Select * from accounts where userName =:userName")
    suspend fun getAccountByUserNameV2(userName: String): AccountDbEntity
}