package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.*
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.view.activity.ViewMain


class ActivityPresenter(
    private val router: Router,
    private val sharedPreferenceAccount: SharedPreferenceAccount
) : MvpPresenter<ViewMain>() {

    override fun onFirstViewAttach() {
        Log.d("TAG", "onFirstViewAttach() called")
        viewState.init()
        super.onFirstViewAttach()
    }

    fun openScreenNews() {
        router.replaceScreen(TopNewsViewPagerScreen(sharedPreferenceAccount.getAccountID()))
    }

    fun openScreenSources() {
        router.replaceScreen(SourcesScreen)
    }

    fun openScreenProfile() {
        router.replaceScreen(ProfileScreen(sharedPreferenceAccount.getAccountID()))
    }

    fun openScreenSearchNews() {
        router.replaceScreen(SearchNewsScreen(sharedPreferenceAccount.getAccountID()))
    }

    fun openScreenAllNews() {
        router.replaceScreen(AllNewsScreen(sharedPreferenceAccount.getAccountID()))
    }

    fun onBackPressedRouter() {
        Log.d("Navigate", " router.exit() ActivityPresenter")
        router.exit()
    }

    fun getAccountSettings() {
        if(sharedPreferenceAccount.getAccountID()!= ACCOUNT_ID_DEFAULT){
            viewState.onCreateSetIconTitleAccount(sharedPreferenceAccount.getAccountLogin())
        }
    }

    fun openFavoritesScreen() {
        router.replaceScreen(FavoritesViewPagerScreen(sharedPreferenceAccount.getAccountID()))
    }

    fun openScreenWebView(url: String) {
       // router.navigateTo(WebViewScreen(url))
    }
}