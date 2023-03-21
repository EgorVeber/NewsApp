package ru.gb.veber.newsapi.domain.repository

import ru.gb.veber.newsapi.domain.models.ApiKeysModel

interface ApiKeysRepository {
    suspend fun insertApiKeys(apiKeysModel: ApiKeysModel)
    suspend fun updateApiKeys(apiKeysModel: ApiKeysModel)
    suspend fun deleteApiKeys(apiKeysModel: ApiKeysModel)
    suspend fun getApiKeys(accountId: Int): List<ApiKeysModel>
    suspend fun getActiveApiKeys(accountId: Int): ApiKeysModel
    suspend fun activateApiKeysByKeyId(keyId: Int)
    suspend fun deactivateApiKeysById(accountId: Int)
}
