package ru.gb.veber.newsapi.presentation.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.gb.veber.newsapi.common.ConnectivityListener
import ru.gb.veber.newsapi.core.FavoritesViewPagerScreen
import ru.gb.veber.newsapi.core.ProfileScreen
import ru.gb.veber.newsapi.core.SearchScreen
import ru.gb.veber.newsapi.core.SourcesScreen
import ru.gb.veber.newsapi.core.TopNewsViewPagerScreen
import ru.gb.veber.newsapi.domain.interactor.MainInteractor
import ru.gb.veber.newsapi.domain.models.CountryModel
import ru.gb.veber.newsapi.domain.models.SourcesModel
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import ru.gb.veber.ui_common.API_KEY_NEWS
import ru.gb.veber.ui_common.TAG_DB_ERROR
import ru.gb.veber.ui_common.TAG_LOAD_NEWS_ERROR
import ru.gb.veber.ui_common.coroutine.SingleSharedFlow
import ru.gb.veber.ui_common.coroutine.launchJob
import javax.inject.Inject

class ActivityMainViewModel @Inject constructor(
    private val mainInteractor: MainInteractor,
    private val connectivityListener: ConnectivityListener,
    private val router: Router,
) : ViewModel() {

    private val mutableFlow: MutableStateFlow<ViewMainState> =
        MutableStateFlow(ViewMainState.StartedState)
    private val flow: StateFlow<ViewMainState> = mutableFlow

    private val connectionState = SingleSharedFlow<Boolean>()
    val connectionFlow = connectionState.asSharedFlow()

    init {
        viewModelScope.launchJob(tryBlock = {
            connectivityListener.status().collect { statusNetwork ->
                connectionState.emit(statusNetwork)
            }
        }, catchBlock = { error ->
            Log.d(TAG_LOAD_NEWS_ERROR, error.toString())
        })
    }

    fun subscribe(): Flow<ViewMainState> {
        return flow
    }

    fun openScreenNews() {
        router.newRootScreen(TopNewsViewPagerScreen(mainInteractor.getAccountID()))
        mutableFlow.value = ViewMainState.HideAllBehavior
    }

    fun openScreenSources() {
        router.newRootScreen(SourcesScreen(mainInteractor.getAccountID()))
        mutableFlow.value = ViewMainState.HideAllBehavior
    }

    fun openScreenProfile() {
        router.newRootScreen(ProfileScreen(mainInteractor.getAccountID()))
        mutableFlow.value = ViewMainState.HideAllBehavior
    }

    fun openScreenSearchNews() {
        router.newRootScreen(SearchScreen(mainInteractor.getAccountID()))
        mutableFlow.value = ViewMainState.HideAllBehavior
    }

    fun openFavoritesScreen() {
        router.newRootScreen(FavoritesViewPagerScreen(mainInteractor.getAccountID()))
        mutableFlow.value = ViewMainState.HideAllBehavior
    }

    fun getAccountSettings() {
        if (mainInteractor.getAccountID() != ACCOUNT_ID_DEFAULT) {
            mutableFlow.value = ViewMainState.OnCreateSetIconTitleAccount(
                mainInteractor.getAccountLogin()
            )
        }
    }

    fun getCheckFirstStartApp() {
        if (!mainInteractor.getCheckFirstStartApp()) {
            fillDataBase()
            mainInteractor.setCheckFirstStartApp()
        }
    }

    private fun fillDataBase() {
        viewModelScope.launchJob(tryBlock = {

            val sourcesList: List<SourcesModel> = mainInteractor.getSources(API_KEY_NEWS)

            val countryStringArray = mainInteractor.getArrayCountry()

            for (source in sourcesList) {
                countryStringArray.forEach { country ->
                    if (source.country == country.value) {
                        source.country = country.key
                    }
                    if (source.language == country.value) {
                        source.language = country.key
                    }
                }
            }

            mainInteractor.countryInsertAll(countryStringArray.map { newCountry ->
                CountryModel(newCountry.key, newCountry.value)
            })

            mainInteractor.sourcesInsertAll(sourcesList)

            mutableFlow.value = ViewMainState.CompletableInsertSources

        }, catchBlock = { error ->
            mutableFlow.value = ViewMainState.ErrorSourcesDownload
            Log.d(TAG_DB_ERROR, error.localizedMessage)
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
