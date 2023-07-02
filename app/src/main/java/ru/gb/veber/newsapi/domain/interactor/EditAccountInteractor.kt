package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.data.AccountDataSource
import ru.gb.veber.newsapi.domain.models.AccountModel
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import javax.inject.Inject

class EditAccountInteractor @Inject constructor(
    private val roomRepoImpl: AccountRepo,
    private val prefsAccount: AccountDataSource,
) {

    suspend fun updateAccount(accountModel: AccountModel, login: String) {
        roomRepoImpl.updateAccount(accountModel)
        prefsAccount.setAccountLogin(login)
    }

    suspend fun getAccount(accountId: Int): AccountModel {
        return roomRepoImpl.getAccountByIdV2(accountId)
    }
}