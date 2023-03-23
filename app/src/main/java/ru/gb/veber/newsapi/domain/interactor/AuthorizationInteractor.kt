package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.data.SharedPreferenceAccount
import ru.gb.veber.newsapi.data.models.room.entity.AccountDbEntity
import ru.gb.veber.newsapi.domain.models.Account
import ru.gb.veber.newsapi.domain.models.ApiKeysModel
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.ApiKeysRepository
import javax.inject.Inject

class AuthorizationInteractor @Inject constructor(
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val accountRepo: AccountRepo,
    private val apiKeysRepository: ApiKeysRepository,
) {

    suspend fun createAccountV2(accountDbEntity: AccountDbEntity) {
        return accountRepo.createAccountV2(accountDbEntity)
    }

    suspend fun getAccountByUserNameV2(userName: String): Account {
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