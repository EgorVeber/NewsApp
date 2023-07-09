package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.common.PrefsAccountHelper
import ru.gb.veber.newsapi.domain.models.ApiKeysModel
import ru.gb.veber.newsapi.domain.repository.ApiKeysRepository
import javax.inject.Inject

class KeysManagementInteractor @Inject constructor(
    private val apiKeysRepository: ApiKeysRepository,
    private val sharedPrefs: PrefsAccountHelper,
) {
    suspend fun getKeys(accountId: Int): List<ApiKeysModel> {
        return apiKeysRepository.getApiKeys(accountId)
            .sortedBy { keysModel -> !keysModel.actived }
    }

    suspend fun addKeys(apiKeysModel: ApiKeysModel): List<ApiKeysModel> {
        apiKeysRepository.insertApiKeys(apiKeysModel)
        return apiKeysRepository.getApiKeys(apiKeysModel.accountId)
            .sortedBy { keysModel -> !keysModel.actived }
    }

    suspend fun setNewKey(apiKeyModel: ApiKeysModel): List<ApiKeysModel> {
        apiKeysRepository.deactivateApiKeysById(apiKeyModel.accountId)
        apiKeysRepository.activateApiKeysByKeyId(apiKeyModel.id)
        sharedPrefs.setActiveKey(apiKeyModel.keyApi)
        return apiKeysRepository.getApiKeys(apiKeyModel.accountId)
            .sortedBy { keysModel -> !keysModel.actived }
    }
}