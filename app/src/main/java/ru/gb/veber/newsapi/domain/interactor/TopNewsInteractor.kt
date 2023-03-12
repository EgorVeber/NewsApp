package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.Country
import ru.gb.veber.newsapi.data.SharedPreferenceAccount
import ru.gb.veber.newsapi.data.models.room.entity.AccountDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.ArticleDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.CountryDbEntity
import ru.gb.veber.newsapi.data.network.ArticlesDTO
import ru.gb.veber.newsapi.domain.repository.NewsRepo
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import ru.gb.veber.newsapi.domain.repository.CountryRepo
import ru.gb.veber.newsapi.core.utils.ALL_COUNTRY
import ru.gb.veber.newsapi.core.utils.ALL_COUNTRY_VALUE
import ru.gb.veber.newsapi.data.mapper.toCountry
import ru.gb.veber.newsapi.model.Article
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


    suspend fun insertArticleV2(
        toArticleDbEntity: ArticleDbEntity,
        articleListHistory: MutableList<Article>,
        title: String?
    ): MutableList<Article> {

        articleRepoImpl.insertArticleV2(toArticleDbEntity)

        articleListHistory.find { articleHistory ->
            articleHistory.title == title
        }?.isHistory = true

        return articleListHistory
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

    suspend fun getCountry(listCountry: MutableList<Country>): Triple<MutableList<String>, Int, MutableList<Country>> {

        val listCountryDbEntity = getCountryV2()

        var newList = listCountry

        newList = listCountryDbEntity.map { countryDbEntity ->
            countryDbEntity.toCountry()
        } as MutableList<Country>

        newList.add(0, Country(ALL_COUNTRY, ALL_COUNTRY_VALUE))

        val list: MutableList<String> =
            newList.map { country -> country.id }.sortedBy { country -> country }
                .toMutableList()

        var index = list.indexOf(getAccountCountry())

        if (index == -1) {
            index = 0
        }

        return Triple(list, index, newList)
    }

    private suspend fun getCountryV2(): List<CountryDbEntity> {
        return countryRepoImpl.getCountryV2()
    }

    private fun getAccountCountry(): String {
        return sharedPreferenceAccount.getAccountCountry()
    }
}
