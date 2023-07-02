package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.data.AccountDataSource
import ru.gb.veber.newsapi.domain.models.AccountModel
import ru.gb.veber.newsapi.domain.models.ArticleModel
import ru.gb.veber.newsapi.domain.models.CountryModel
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import ru.gb.veber.newsapi.domain.repository.CountryRepo
import ru.gb.veber.newsapi.domain.repository.NewsRepo
import javax.inject.Inject

class TopNewsInteractor @Inject constructor(
    private val sharedPreferenceAccount: AccountDataSource,
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
        articleRepoImpl.insertArticleV2(articleModel, accountId)
    }

    suspend fun deleteArticleByIdFavoritesV2(toString: String, accountId: Int) {
        articleRepoImpl.deleteArticleByIdFavoritesV2(toString, accountId)
    }

    suspend fun getAccountByIdV2(accountId: Int): AccountModel {
        return accountRepoImpl.getAccountByIdV2(accountId)
    }

    fun getAccountCountryCode(): String {
        return sharedPreferenceAccount.getAccountCountryCode()
    }

    suspend fun getTopicalHeadlinesCategoryCountryV2(
        category: String,
        country: String,
        key: String,
    ): List<ArticleModel> =
        newsRepoImpl.getTopicalHeadlinesCategoryCountryV2(category, country, key).articles


    suspend fun getArticleById(accountId: Int): List<ArticleModel> =
        articleRepoImpl.getArticleByIdV2(accountId)

    suspend fun getCountry(): List<CountryModel> = countryRepoImpl.getCountry()


    fun getAccountCountry(): String {
        return sharedPreferenceAccount.getAccountCountry()
    }
}
