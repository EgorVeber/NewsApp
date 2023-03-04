package ru.gb.veber.newsapi.view.activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.gb.veber.newsapi.core.FavoritesViewPagerScreen
import ru.gb.veber.newsapi.core.ProfileScreen
import ru.gb.veber.newsapi.core.SearchScreen
import ru.gb.veber.newsapi.core.SourcesScreen
import ru.gb.veber.newsapi.core.TopNewsViewPagerScreen
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.network.NewsRepo
import ru.gb.veber.newsapi.model.repository.room.CountryRepo
import ru.gb.veber.newsapi.model.repository.room.SourcesRepo
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.API_KEY_NEWS
import ru.gb.veber.newsapi.utils.ERROR_DB
import ru.gb.veber.newsapi.utils.extentions.launchJob
import ru.gb.veber.newsapi.utils.mapper.newCountryDbEntity
import ru.gb.veber.newsapi.utils.mapper.toSourcesDbEntity
import javax.inject.Inject

class ActivityMainViewModel @Inject constructor(
    private val newsRepoImpl: NewsRepo,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val router: Router,
    private val sourcesRepoImpl: SourcesRepo,
    private val countryRepoImpl: CountryRepo,
) : ViewModel() {

    private val mutableFlow: MutableStateFlow<ViewMainState> =
        MutableStateFlow(ViewMainState.StartedState)
    private val flow: StateFlow<ViewMainState> = mutableFlow

    fun subscribe(): Flow<ViewMainState> {
        return flow
    }

    fun openScreenNews() {
        router.newRootScreen(TopNewsViewPagerScreen(sharedPreferenceAccount.getAccountID()))
        mutableFlow.value = ViewMainState.HideAllBehavior
    }

    fun openScreenSources() {
        router.newRootScreen(SourcesScreen(sharedPreferenceAccount.getAccountID()))
        mutableFlow.value = ViewMainState.HideAllBehavior
    }

    fun openScreenProfile() {
        router.newRootScreen(ProfileScreen(sharedPreferenceAccount.getAccountID()))
        mutableFlow.value = ViewMainState.HideAllBehavior
    }

    fun openScreenSearchNews() {
        router.newRootScreen(SearchScreen(sharedPreferenceAccount.getAccountID()))
        mutableFlow.value = ViewMainState.HideAllBehavior
    }

    fun openFavoritesScreen() {
        router.newRootScreen(FavoritesViewPagerScreen(sharedPreferenceAccount.getAccountID()))
        mutableFlow.value = ViewMainState.HideAllBehavior
    }

    fun getAccountSettings() {
        if (sharedPreferenceAccount.getAccountID() != ACCOUNT_ID_DEFAULT) {
            mutableFlow.value = ViewMainState.OnCreateSetIconTitleAccount(
                sharedPreferenceAccount.getAccountLogin()
            )
        }
    }

    fun getCheckFirstStartApp() {
        if (!sharedPreferenceAccount.getCheckFirstStartApp()) {
            fillDataBase()
            sharedPreferenceAccount.setCheckFirstStartApp()
        }
    }

    private fun fillDataBase() {
        viewModelScope.launchJob(tryBlock = {

            val sourcesApi = newsRepoImpl.getSourcesV2(key = API_KEY_NEWS)

            val countryStringArray = sharedPreferenceAccount.getArrayCountry()

            for (source in sourcesApi.sources) {
                countryStringArray.forEach { country ->
                    if (source.country == country.value) {
                        source.country = country.key
                    }
                    if (source.language == country.value) {
                        source.language = country.key
                    }
                }
            }

            countryRepoImpl.insertAllV2(countryStringArray.map { newCountry ->
                newCountryDbEntity(newCountry.key, newCountry.value)
            })

            sourcesRepoImpl.insertAllV2(sourcesApi.sources.map { sourcesDTO ->
                sourcesDTO.toSourcesDbEntity()
            })

            mutableFlow.value = ViewMainState.CompletableInsertSources

        }, catchBlock = { error ->
            mutableFlow.value = ViewMainState.ErrorSourcesDownload
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    fun onBackPressedRouter() {
        router.exit()
    }

    sealed class ViewMainState() {
        data class OnCreateSetIconTitleAccount(val accountLogin: String) : ViewMainState()
        object CompletableInsertSources : ViewMainState()
        object HideAllBehavior : ViewMainState()
        object ErrorSourcesDownload : ViewMainState()
        object StartedState : ViewMainState()
    }
}