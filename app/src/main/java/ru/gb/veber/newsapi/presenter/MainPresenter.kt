package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter


class MainPresenter(private val router: Router) : MvpPresenter<ViewMain>() {

    override fun onFirstViewAttach() {
        viewState.init()
       // router.replaceScreen(FragmentNewsScreen)
        super.onFirstViewAttach()
    }

    fun openScreenNews() {
        router.navigateTo(FragmentNewsScreen)
    }

    fun openScreenSources() {
        router.navigateTo(FragmentSourcesScreen)
    }

    fun openScreenProfile() {
        router.navigateTo(FragmentProfileScreen)
    }

    fun onBackPressedRouter() {
        Log.d("Back", "onBackPressedRouter() Main")
        router.exit()
    }
}