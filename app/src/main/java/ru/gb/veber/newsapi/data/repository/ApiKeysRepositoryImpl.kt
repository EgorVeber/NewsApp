package ru.gb.veber.newsapi.data.repository

import ru.gb.veber.newsapi.data.mapper.toApiKeysEntity
import ru.gb.veber.newsapi.data.mapper.toApiKeysModel
import ru.gb.veber.newsapi.data.models.room.dao.ApiKeysDao
import ru.gb.veber.newsapi.domain.models.ApiKeysModel
import ru.gb.veber.newsapi.domain.repository.ApiKeysRepository

class ApiKeysRepositoryImpl(private val apiKeysDao: ApiKeysDao) : ApiKeysRepository {

    override suspend fun insertApiKeys(apiKeysModel: ApiKeysModel) {
        apiKeysDao.insertApiKeys(apiKeysModel.toApiKeysEntity())
    }

    override suspend fun updateApiKeys(apiKeysModel: ApiKeysModel) {
        apiKeysDao.updateApiKeys(apiKeysModel.toApiKeysEntity())
    }

    override suspend fun deleteApiKeys(apiKeysModel: ApiKeysModel) {
        apiKeysDao.deleteApiKeys(apiKeysModel.toApiKeysEntity())
    }

    override suspend fun getApiKeys(accountId: Int): List<ApiKeysModel> {
        return apiKeysDao.getApiKeys(accountId).map { apiKeysDb -> apiKeysDb.toApiKeysModel() }
    }

    override suspend fun getActiveApiKeys(accountId: Int): ApiKeysModel {
        return apiKeysDao.getActiveApiKeys(accountId).toApiKeysModel()
    }

    override suspend fun activateApiKeysByKeyId(keyId: Int) {
        apiKeysDao.activateApiKeysById(keyId)
    }

    override suspend fun deactivateApiKeysById(accountId: Int) {
        apiKeysDao.deactivateApiKeysById(accountId)
    }
}
