package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.*
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.model.repository.room.SourcesRepoImpl
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.ERROR_DB
import ru.gb.veber.newsapi.utils.sourcesDtoToEntity
import ru.gb.veber.newsapi.view.activity.ViewMain


class ActivityPresenter(
    private val newsRepoImpl: NewsRepoImpl,
    private val router: Router,
    private val sourcesRepoImpl: SourcesRepoImpl,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
) : MvpPresenter<ViewMain>() {

    override fun onFirstViewAttach() {
        Log.d("TAG", "onFirstViewAttach() called")
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
        router.newRootScreen(SearchNewsScreen(sharedPreferenceAccount.getAccountID()))
        viewState.hideAllBehavior()
    }

    fun openFavoritesScreen() {
        router.newRootScreen(FavoritesViewPagerScreen(sharedPreferenceAccount.getAccountID()))
        viewState.hideAllBehavior()
    }

    fun onBackPressedRouter() {
        Log.d("Navigate", " router.exit() ActivityPresenter")
        router.exit()
    }

    fun getAccountSettings() {
        if (sharedPreferenceAccount.getAccountID() != ACCOUNT_ID_DEFAULT) {
            viewState.onCreateSetIconTitleAccount(sharedPreferenceAccount.getAccountLogin())
        }
    }

    fun openScreenWebView(url: String) {
        // router.navigateTo(WebViewScreen(url))
    }

    fun getCheckFirstStartApp() {
        if (!sharedPreferenceAccount.getCheckFirstStartApp()) {
            fillDataBaseSources()
            sharedPreferenceAccount.setCheckFirstStartApp()
        }
    }

    private fun fillDataBaseSources() {
        Log.d("TAG", "fillDataBaseSources")
        newsRepoImpl.getSources().subscribe({
            Log.d(ERROR_DB, it.sources.toString())
            sourcesRepoImpl.insertAll(it.sources.map(::sourcesDtoToEntity)).subscribe({
                viewState.completableInsertSources()
            }, {
                Log.d(ERROR_DB, "insertAll " + it.localizedMessage)
            })
        }, {
            Log.d(ERROR_DB, " getSources " + it.localizedMessage)
        })
    }
}