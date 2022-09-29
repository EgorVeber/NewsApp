package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.view.profile.FragmentProfileMainView

class FragmentProfileMainPresenter(private val router: Router) :
    MvpPresenter<FragmentProfileMainView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        Log.d("Back", "onBackPressedRouter() Profile")
        return true
    }

    fun openScreenProfile(accountId: Int) {
        router.replaceScreen(FragmentProfileScreen(accountId))
    }

    fun openScreenAuthorization() {
        router.replaceScreen(FragmentAuthorizationScreen)
    }
}