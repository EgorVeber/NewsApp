package ru.gb.veber.newsapi.view.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.core.TopNewsViewPagerScreen
import ru.gb.veber.newsapi.core.SourcesScreen
import ru.gb.veber.newsapi.core.ProfileScreen
import ru.gb.veber.newsapi.core.SearchScreen
import ru.gb.veber.newsapi.core.FavoritesViewPagerScreen
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.network.NewsRepo
import ru.gb.veber.newsapi.model.repository.room.CountryRepo
import ru.gb.veber.newsapi.model.repository.room.SourcesRepo
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.API_KEY_NEWS
import ru.gb.veber.newsapi.utils.mapper.mapToCountry
import ru.gb.veber.newsapi.utils.mapper.toSourcesDbEntity
import javax.inject.Inject

class ActivityMainViewModel @Inject constructor(
    private val newsRepoImpl: NewsRepo,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val router: Router,
    private val sourcesRepoImpl: SourcesRepo,
    private val countryRepoImpl: CountryRepo
) : ViewModel() {

    private val mutableFlow: MutableLiveData<ViewMainState> = MutableLiveData()
    private val flow: LiveData<ViewMainState> = mutableFlow

    fun subscribe(): LiveData<ViewMainState> {
        mutableFlow.value = ViewMainState.Init
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

    fun onBackPressedRouter() {
        router.exit()
    }

    fun getAccountSettings() {
        if (sharedPreferenceAccount.getAccountID() != ACCOUNT_ID_DEFAULT) {
            mutableFlow.value = ViewMainState.OnCreateSetIconTitleAccount(
                sharedPreferenceAccount.getAccountLogin())
        }
    }

    fun getCheckFirstStartApp() {
        if (!sharedPreferenceAccount.getCheckFirstStartApp()) {
            fillDataBase()
            sharedPreferenceAccount.setCheckFirstStartApp()
        }
    }

    private fun fillDataBase() {
        newsRepoImpl.getSources(key = API_KEY_NEWS).subscribe({ source ->
            val country = sharedPreferenceAccount.getArrayCountry()
            for (i in source.sources) {
                country.forEach {
                    if (i.country == it.value) {
                        i.country = it.key
                    }
                    if (i.language == it.value) {
                        i.language = it.key
                    }
                }
            }
            countryRepoImpl.insertAll(country.map { mapToCountry(it.key, it.value) }).andThen(
                sourcesRepoImpl.insertAll(source.sources.map { sourcesDTO ->
                    sourcesDTO.toSourcesDbEntity()
                })
            )
                .subscribe({
                    mutableFlow.value = ViewMainState.CompletableInsertSources
                }, {
                    mutableFlow.value = ViewMainState.ErrorSourcesDownload
                })
        }, {
            mutableFlow.value = ViewMainState.ErrorSourcesDownload
        })
    }

    sealed class ViewMainState() {
        object Init : ViewMainState()
        data class OnCreateSetIconTitleAccount(val accountLogin: String) : ViewMainState()
        object CompletableInsertSources : ViewMainState()
        object HideAllBehavior : ViewMainState()
        object ErrorSourcesDownload : ViewMainState()
    }
}