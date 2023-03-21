package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.ACCOUNT_LOGIN_DEFAULT
import ru.gb.veber.newsapi.common.utils.API_KEY_EMPTY
import ru.gb.veber.newsapi.data.SharedPreferenceAccount
import ru.gb.veber.newsapi.data.models.room.entity.AccountDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.ArticleDbEntity
import ru.gb.veber.newsapi.domain.models.Account
import ru.gb.veber.newsapi.domain.models.Sources
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.AccountSourcesRepo
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import javax.inject.Inject

class AccountInteractor
@Inject constructor(
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val accountRepoImpl: AccountRepo,
    private val articleRepoImpl: ArticleRepo,
    private val accountSourcesRepoImpl: AccountSourcesRepo,
) {

    suspend fun deleteAccountV2(accountId: Int) {
        return accountRepoImpl.deleteAccountV2(accountId)
    }

    suspend fun getAccountByIdV2(accountId: Int): Account {
        return accountRepoImpl.getAccountByIdV2(accountId)
    }

    suspend fun updateAccountV2(accountDbEntity: AccountDbEntity) {
        return accountRepoImpl.updateAccountV2(accountDbEntity)
    }

    suspend fun updateAccountByIdV2(id: Int, checked: Boolean) {
        return accountRepoImpl.updateAccountByIdV2(id, checked)
    }

    suspend fun deleteArticleIsHistoryByIdV2(accountId: Int) {
        return articleRepoImpl.deleteArticleIsHistoryByIdV2(accountId)
    }

    suspend fun deleteArticleIsFavoriteByIdV2(accountId: Int) {
        return articleRepoImpl.deleteArticleIsFavoriteByIdV2(accountId)
    }

    suspend fun getArticleByIdV2(accountId: Int): List<ArticleDbEntity> {
        return articleRepoImpl.getArticleByIdV2(accountId)
    }

    suspend fun deleteSourcesV2(accountId: Int) {
        accountSourcesRepoImpl.deleteSourcesV2(accountId)
    }

    suspend fun getLikeSourcesFromAccountV2(accountId: Int): List<Sources> {
        return accountSourcesRepoImpl.getLikeSourcesFromAccountV2(accountId)
    }

    fun setTheme(key: Int) {
        sharedPreferenceAccount.setTheme(key)
    }

    fun getThemePrefs(): Int {
        return sharedPreferenceAccount.getThemePrefs()
    }

    fun accountLogout(){
        sharedPreferenceAccount.setAccountID(ACCOUNT_ID_DEFAULT)
        sharedPreferenceAccount.setAccountLogin(ACCOUNT_LOGIN_DEFAULT)
        sharedPreferenceAccount.setActiveKey(API_KEY_EMPTY)
    }
}