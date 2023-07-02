package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.ACCOUNT_LOGIN_DEFAULT
import ru.gb.veber.newsapi.common.utils.API_KEY_EMPTY
import ru.gb.veber.newsapi.data.AccountDataSource
import ru.gb.veber.newsapi.domain.models.AccountModel
import ru.gb.veber.newsapi.domain.models.ArticleModel
import ru.gb.veber.newsapi.domain.models.SourcesModel
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.AccountSourcesRepo
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import javax.inject.Inject

class AccountInteractor
@Inject constructor(
    private val sharedPreferenceAccount: AccountDataSource,
    private val accountRepoImpl: AccountRepo,
    private val articleRepoImpl: ArticleRepo,
    private val accountSourcesRepoImpl: AccountSourcesRepo,
) {

    suspend fun deleteAccountV2(accountId: Int) {
        return accountRepoImpl.deleteAccountV2(accountId)
    }

    suspend fun getAccountByIdV2(accountId: Int): AccountModel {
        return accountRepoImpl.getAccountByIdV2(accountId)
    }

    suspend fun updateAccount(accountModel: AccountModel) {
        return accountRepoImpl.updateAccount(accountModel)
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

    suspend fun getArticleByIdV2(accountId: Int): List<ArticleModel> {
        return articleRepoImpl.getArticleByIdV2(accountId)
    }

    suspend fun deleteSourcesV2(accountId: Int) {
        accountSourcesRepoImpl.deleteSourcesV2(accountId)
    }

    suspend fun getLikeSourcesFromAccountV2(accountId: Int): List<SourcesModel> {
        return accountSourcesRepoImpl.getLikeSources(accountId)
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