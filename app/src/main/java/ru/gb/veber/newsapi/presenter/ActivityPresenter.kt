package ru.gb.veber.newsapi.presenter

import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.*
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.network.NewsRepo
import ru.gb.veber.newsapi.model.repository.room.CountryRepo
import ru.gb.veber.newsapi.model.repository.room.SourcesRepo
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.mapToCountry
import ru.gb.veber.newsapi.utils.sourcesDtoToEntity
import ru.gb.veber.newsapi.view.activity.ViewMain
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KClass


class ActivityPresenter : MvpPresenter<ViewMain>() {

    @Inject
    lateinit var newsRepoImpl: NewsRepo

    @Inject
    lateinit var sharedPreferenceAccount: SharedPreferenceAccount

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var sourcesRepoImpl: SourcesRepo

    @Inject
    lateinit var countryRepoImpl: CountryRepo

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
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