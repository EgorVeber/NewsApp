package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.data.AccountDataSource
import ru.gb.veber.newsapi.domain.models.CountryModel
import ru.gb.veber.newsapi.domain.models.SourcesModel
import ru.gb.veber.newsapi.domain.repository.CountryRepo
import ru.gb.veber.newsapi.domain.repository.NewsRepo
import ru.gb.veber.newsapi.domain.repository.SourcesRepo
import javax.inject.Inject

class MainInteractor @Inject constructor(
    private val newsRepoImpl: NewsRepo,
    private val sharedPreferenceAccount: AccountDataSource,
    private val sourcesRepoImpl: SourcesRepo,
    private val countryRepoImpl: CountryRepo,
) {

    suspend fun getSources(api_key: String): List<SourcesModel> =
        newsRepoImpl.getSourcesV2(key = api_key).sources

    suspend fun countryInsertAll(countryList: List<CountryModel>) {
        countryRepoImpl.insertAll(countryList)
    }

    suspend fun sourcesInsertAll(sourcesList: List<SourcesModel>) {
        sourcesRepoImpl.insertAll(sourcesList)
    }

    fun getArrayCountry(): HashMap<String, String> {
        return sharedPreferenceAccount.getArrayCountry()
    }

    fun getAccountID(): Int {
        return sharedPreferenceAccount.getAccountID()
    }

    fun getAccountLogin(): String {
        return sharedPreferenceAccount.getAccountLogin()
    }

    fun getCheckFirstStartApp(): Boolean {
        return sharedPreferenceAccount.getCheckFirstStartApp()
    }

    fun setCheckFirstStartApp() {
        sharedPreferenceAccount.setCheckFirstStartApp()
    }


}