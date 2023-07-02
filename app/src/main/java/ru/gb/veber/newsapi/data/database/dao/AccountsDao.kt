package ru.gb.veber.newsapi.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.gb.veber.newsapi.data.database.entity.AccountEntity

@Dao
interface AccountsDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun createAccount(accountEntity: AccountEntity)

    @Update
    suspend fun updateAccount(accountEntity: AccountEntity)

    @Query("Update accounts set save_history = :saveHistory  where id = :accountId")
    suspend fun updateAccountByIdV2(accountId: Int, saveHistory: Boolean)

    @Query("Delete from accounts where id = :accountId")
    suspend fun deleteAccountV2(accountId: Int)

    @Query("Delete from accounts")
    suspend fun deleteAllAccountV2()

    @Query("Select * from accounts where id =:accountId")
    suspend fun getAccountByIdV2(accountId: Int): AccountEntity

    @Query("Select * from accounts where userName =:userName")
    suspend fun getAccountByUserNameV2(userName: String): AccountEntity
}
