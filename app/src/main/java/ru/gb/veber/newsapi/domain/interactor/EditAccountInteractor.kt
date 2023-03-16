package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.data.SharedPreferenceAccount
import ru.gb.veber.newsapi.data.models.room.entity.AccountDbEntity
import ru.gb.veber.newsapi.domain.models.Account
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import javax.inject.Inject

class EditAccountInteractor @Inject constructor(
    private val roomRepoImpl: AccountRepo,
    private val prefsAccount: SharedPreferenceAccount,
) {

    suspend fun updateAccount(accountDbEntity: AccountDbEntity) {
        roomRepoImpl.updateAccountV2(accountDbEntity)
    }

    suspend fun getAccount(accountId: Int): Account {
        return roomRepoImpl.getAccountByIdV2(accountId)
    }

    fun setAccount(login: String) {
        prefsAccount.setAccountLogin(login)
    }
}