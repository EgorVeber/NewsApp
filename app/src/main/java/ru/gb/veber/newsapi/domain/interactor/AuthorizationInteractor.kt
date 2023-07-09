package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.common.PrefsAccountHelper
import ru.gb.veber.newsapi.domain.models.AccountModel
import ru.gb.veber.newsapi.domain.models.ApiKeysModel
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.ApiKeysRepository
import javax.inject.Inject

class AuthorizationInteractor @Inject constructor(
    private val sharedPreferenceAccount: PrefsAccountHelper,
    private val accountRepo: AccountRepo,
    private val apiKeysRepository: ApiKeysRepository,
) {

    suspend fun createAccount(accountModel: AccountModel) {
        return accountRepo.createAccount(accountModel)
    }

    suspend fun getAccountByUserNameV2(userName: String): AccountModel {
        return accountRepo.getAccountByUserNameV2(userName)
    }

    suspend fun getApiKeys(accountId: Int): List<ApiKeysModel> {
        return apiKeysRepository.getApiKeys(accountId)
    }

    fun setAccountID(id: Int) {
        sharedPreferenceAccount.setAccountID(id)
    }

    fun setAccountLogin(login: String) {
        sharedPreferenceAccount.setAccountLogin(login)
    }

    fun setActiveKey(apiKey: String) {
        return sharedPreferenceAccount.setActiveKey(apiKey)
    }
}