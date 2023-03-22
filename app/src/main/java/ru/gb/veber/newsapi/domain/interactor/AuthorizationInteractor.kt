package ru.gb.veber.newsapi.domain.interactor

import android.content.Context
import ru.gb.veber.newsapi.common.utils.API_KEY_KEY
import ru.gb.veber.newsapi.common.utils.FILE_SETTINGS
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.data.SharedPreferenceAccount
import ru.gb.veber.newsapi.data.mapper.toAccount
import ru.gb.veber.newsapi.data.models.room.entity.AccountDbEntity
import ru.gb.veber.newsapi.domain.models.Account
import ru.gb.veber.newsapi.domain.models.ApiKeysModel
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.ApiKeysRepository
import javax.inject.Inject

class AuthorizationInteractor
@Inject constructor(
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val accountRepoImpl: AccountRepo,
    private val apiKeysRepository: ApiKeysRepository,
) {

    suspend fun createAccountV2(accountDbEntity: AccountDbEntity) {
        return accountRepoImpl.createAccountV2(accountDbEntity)
    }

    suspend fun getAccountByUserNameV2(userName: String): Account {
        return accountRepoImpl.getAccountByUserNameV2(userName)
    }

    suspend fun getApiKeys(accountId: Int): List<ApiKeysModel>{
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