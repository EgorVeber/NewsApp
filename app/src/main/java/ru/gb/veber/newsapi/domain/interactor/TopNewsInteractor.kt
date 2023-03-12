package ru.gb.veber.newsapi.domain.interactor

import android.util.Log
import ru.gb.veber.newsapi.common.utils.ALL_COUNTRY
import ru.gb.veber.newsapi.common.utils.ALL_COUNTRY_VALUE
import ru.gb.veber.newsapi.data.SharedPreferenceAccount
import ru.gb.veber.newsapi.data.mapper.toCountry
import ru.gb.veber.newsapi.data.models.network.ArticlesDTO
import ru.gb.veber.newsapi.data.models.room.entity.AccountDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.ArticleDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.CountryDbEntity
import ru.gb.veber.newsapi.domain.models.Account
import ru.gb.veber.newsapi.domain.models.Article
import ru.gb.veber.newsapi.domain.models.Country
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import ru.gb.veber.newsapi.domain.repository.CountryRepo
import ru.gb.veber.newsapi.domain.repository.NewsRepo
import javax.inject.Inject

class TopNewsInteractor @Inject constructor(
    private val sharedPreferenceAccount: SharedPreferenceAccount,
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

    suspend fun updateAccountV2(toAccountDbEntity: AccountDbEntity) {
        accountRepoImpl.updateAccountV2(toAccountDbEntity)
    }

    suspend fun insertArticleV2(articleDb: ArticleDbEntity) {
        articleRepoImpl.insertArticleV2(articleDb)
    }

    suspend fun deleteArticleByIdFavoritesV2(toString: String, accountId: Int) {
        articleRepoImpl.deleteArticleByIdFavoritesV2(toString, accountId)
    }

    suspend fun getAccountByIdV2(accountId: Int): Account {
        return accountRepoImpl.getAccountByIdV2(accountId)
    }

    fun getAccountCountryCode(): String {
        return sharedPreferenceAccount.getAccountCountryCode()
    }

    suspend fun getTopicalHeadlinesCategoryCountryV2(
        category: String,
        country: String,
        key: String,
    ): ArticlesDTO {
        return newsRepoImpl.getTopicalHeadlinesCategoryCountryV2(category, country, key)
    }

    suspend fun getArticleByIdV2(accountId: Int): List<ArticleDbEntity> {
        return articleRepoImpl.getArticleByIdV2(accountId)
    }

    suspend fun getCountryV2(): List<CountryDbEntity> {
        return countryRepoImpl.getCountryV2()
    }

    fun getAccountCountry(): String {
        return sharedPreferenceAccount.getAccountCountry()
    }
}
