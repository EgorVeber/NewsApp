package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.view.profile.FragmentProfileMain
import ru.gb.veber.newsapi.view.profile.FragmentProfileMainView
import ru.gb.veber.newsapi.view.profile.FragmentProfileView

class FragmentProfileMainPresenter(private val router: Router) :
    MvpPresenter<FragmentProfileMainView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun onBackPressedRouter(): Boolean {
        router.backTo(FragmentViewPagerNewsScreen)
        Log.d("Back", "onBackPressedRouter() Profile")
        return true
    }

    fun openScreenProfile() {
        router.replaceScreen(FragmentProfileScreen)
    }

    fun openScreenAuthorization() {
        router.replaceScreen(FragmentAuthorizationScreen)
    }
}