package ru.gb.veber.newsapi.data.models.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.gb.veber.newsapi.data.models.room.entity.ArticleDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.ApiKeysDbEntity

@Dao
interface ApiKeysDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertApiKeys(apiKeysDbEntity: ApiKeysDbEntity)

    @Update
    suspend fun updateApiKeys(apiKeysDbEntity: ApiKeysDbEntity)

    @Delete
    suspend fun deleteApiKeys(apiKeysDbEntity: ApiKeysDbEntity)

    @Query("Select * from api_keys where account_id =:accountId")
    suspend fun getApiKeys(accountId: Int): List<ApiKeysDbEntity>

    @Query("Select * from api_keys where account_id =:accountId and actived = 'true'")
    suspend fun getActiveApiKeys(accountId: Int): ApiKeysDbEntity
}