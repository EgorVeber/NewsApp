package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.data.SharedPreferenceAccount
import ru.gb.veber.newsapi.data.models.network.SourcesRequestDTO
import ru.gb.veber.newsapi.data.models.room.entity.CountryDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.SourcesDbEntity
import ru.gb.veber.newsapi.domain.repository.CountryRepo
import ru.gb.veber.newsapi.domain.repository.NewsRepo
import ru.gb.veber.newsapi.domain.repository.SourcesRepo
import javax.inject.Inject

class MainInteractor
@Inject constructor(
    private val newsRepoImpl: NewsRepo,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val sourcesRepoImpl: SourcesRepo,
    private val countryRepoImpl: CountryRepo
) {

    suspend fun getSourcesV2(api_key: String): SourcesRequestDTO {
        return newsRepoImpl.getSourcesV2(key = api_key)
    }

    suspend fun countryInsertAllV2(countryList: List<CountryDbEntity>) {
        countryRepoImpl.insertAllV2(countryList)
    }

    suspend fun sourcesInsertAllV2(sourcesList: List<SourcesDbEntity>) {
        sourcesRepoImpl.insertAllV2(sourcesList)
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