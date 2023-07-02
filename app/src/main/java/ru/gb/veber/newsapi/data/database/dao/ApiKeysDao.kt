package ru.gb.veber.newsapi.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.gb.veber.newsapi.data.database.entity.ApiKeysEntity

@Dao
interface ApiKeysDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertApiKeys(apiKeysEntity: ApiKeysEntity)
    @Update
    suspend fun updateApiKeys(apiKeysEntity: ApiKeysEntity)

    @Delete
    suspend fun deleteApiKeys(apiKeysEntity: ApiKeysEntity)

    @Query("Select * from api_keys where account_id =:accountId")
    suspend fun getApiKeys(accountId: Int): List<ApiKeysEntity>

    @Query("Select * from api_keys where account_id =:accountId and actived = 'true'")
    suspend fun getActiveApiKeys(accountId: Int): ApiKeysEntity

    @Query("Update api_keys set actived = 1  where id = :keyId")
    suspend fun activateApiKeysById(keyId: Int)

    @Query("Update api_keys set actived = 0  where account_id = :accountId")
    suspend fun deactivateApiKeysById(accountId: Int)
}