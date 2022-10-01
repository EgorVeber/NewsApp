package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.FragmentAuthorizationScreen
import ru.gb.veber.newsapi.view.profile.account.AccountView

class AccountPresenter(private val router: Router) :
    MvpPresenter<AccountView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }
    fun onBackPressedRouter(): Boolean {
        router.exit()
        Log.d("Back", "onBackPressedRouter() Profile")
        return true
    }

    fun logout() {
        router.replaceScreen(FragmentAuthorizationScreen)
        viewState.logout()
    }
}