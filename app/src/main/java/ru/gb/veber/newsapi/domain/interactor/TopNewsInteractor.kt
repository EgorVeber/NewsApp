package ru.gb.veber.newsapi.domain.interactor

import android.util.Log
import ru.gb.veber.newsapi.common.PrefsAccountHelper
import ru.gb.veber.newsapi.domain.models.AccountModel
import ru.gb.veber.newsapi.domain.models.ArticleModel
import ru.gb.veber.newsapi.domain.models.CountryModel
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import ru.gb.veber.newsapi.domain.repository.CountryRepo
import ru.gb.veber.newsapi.domain.repository.NewsRepo
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import javax.inject.Inject

class TopNewsInteractor @Inject constructor(
    private val sharedPreferenceAccount: PrefsAccountHelper,
    private val newsRepoImpl: NewsRepo,
    private val articleRepoImpl: ArticleRepo,
    private val accountRepoImpl: AccountRepo,
    private val countryRepoImpl: CountryRepo,
) {
    fun setAccountCountryCode(countryCode: String) {
        sharedPreferenceAccount.setAccountCountryCode(countryCode)
    }

    fun setAccountCountry(country: String) {
        sharedPreferenceAccount.setAccountCountry(country)
    }

    suspend fun updateAccount(accountModel: AccountModel) {
        accountRepoImpl.updateAccount(accountModel)
    }

    suspend fun insertArticle(articleModel: ArticleModel, accountId: Int) {
        articleRepoImpl.insertArticle(articleModel, accountId)
    }

    suspend fun deleteArticleByIdFavoritesV2(toString: String, accountId: Int) {
        articleRepoImpl.deleteArticleByIdFavorites(toString, accountId)
    }

    suspend fun getAccount(accountId: Int): AccountModel {
        return accountRepoImpl.getAccountByIdV2(accountId)
    }

    fun getAccountCountryCode(): String {
        return sharedPreferenceAccount.getAccountCountryCode()
    }

    suspend fun getArticleById(accountId: Int): List<ArticleModel> =
        articleRepoImpl.getArticleByIdV2(accountId)

    suspend fun getCountry(): List<CountryModel> = countryRepoImpl.getCountry()

    suspend fun getNews(
        category: String,
        countryCode: String,
        key: String,
        accountId: Int,
    ): List<ArticleModel> {
        val articlesApi = getTopicalHeadlinesCategoryCountry(category, countryCode, key)
        if (accountId == ACCOUNT_ID_DEFAULT) return articlesApi

        val articlesHistory = getArticleById(accountId)

        articlesHistory.forEach {
            Log.d("articlesHistory",it.isFavorites.toString() +  it.title)
        }

        articlesHistory.forEach { articleHistory ->
            articlesApi.forEach { articleApi ->
                if (articleHistory.title == articleApi.title) {
                    if (articleHistory.isFavorites) {
                        articleApi.isFavorites = true
                    }
                    if (articleHistory.isHistory) {
                        articleApi.isHistory = true
                    }
                }
            }
        }

        return articlesApi
    }

    private suspend fun getTopicalHeadlinesCategoryCountry(
        category: String,
        country: String,
        key: String,
    ): List<ArticleModel> =
        newsRepoImpl.getTopicalHeadlinesCategoryCountryV2(category, country, key).articles

    fun getAccountCountry(): String {
        return sharedPreferenceAccount.getAccountCountry()
    }
}
