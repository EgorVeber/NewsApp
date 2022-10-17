package ru.gb.veber.newsapi.presenter

import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.*
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.network.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.CountryRepoImpl
import ru.gb.veber.newsapi.model.repository.room.SourcesRepoImpl
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.mapToCountry
import ru.gb.veber.newsapi.utils.sourcesDtoToEntity
import ru.gb.veber.newsapi.view.activity.ViewMain


class ActivityPresenter(
    private val newsRepoImpl: NewsRepoImpl,
    private val router: Router,
    private val sourcesRepoImpl: SourcesRepoImpl,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val countryRepoImpl: CountryRepoImpl,
) : MvpPresenter<ViewMain>() {

    override fun onFirstViewAttach() {
        viewState.init()
        super.onFirstViewAttach()
    }

    fun openScreenNews() {
        router.newRootScreen(TopNewsViewPagerScreen(sharedPreferenceAccount.getAccountID()))
        viewState.hideAllBehavior()
    }

    fun openScreenSources() {
        router.newRootScreen(SourcesScreen(sharedPreferenceAccount.getAccountID()))
        viewState.hideAllBehavior()
    }

    fun openScreenProfile() {
        router.newRootScreen(ProfileScreen(sharedPreferenceAccount.getAccountID()))
        viewState.hideAllBehavior()
    }

    fun openScreenSearchNews() {
        router.newRootScreen(SearchScreen(sharedPreferenceAccount.getAccountID()))
        viewState.hideAllBehavior()
    }

    fun openFavoritesScreen() {
        router.newRootScreen(FavoritesViewPagerScreen(sharedPreferenceAccount.getAccountID()))
        viewState.hideAllBehavior()
    }

    fun onBackPressedRouter() {
        router.exit()
    }

    fun getAccountSettings() {
        if (sharedPreferenceAccount.getAccountID() != ACCOUNT_ID_DEFAULT) {
            viewState.onCreateSetIconTitleAccount(sharedPreferenceAccount.getAccountLogin())
        }
    }

    fun getCheckFirstStartApp() {
        if (!sharedPreferenceAccount.getCheckFirstStartApp()) {
            fillDataBase()
            sharedPreferenceAccount.setCheckFirstStartApp()
        }
    }

    private fun fillDataBase() {
        newsRepoImpl.getSources().subscribe({ source ->
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
                sourcesRepoImpl.insertAll(source.sources.map(::sourcesDtoToEntity)))
                .subscribe({
                    viewState.completableInsertSources()
                }, {
                        viewState.errorSourcesDownload()
                })
        }, {
            viewState.errorSourcesDownload()
        })
    }
}