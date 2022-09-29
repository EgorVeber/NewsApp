package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.view.activity.ViewMain


class ActivityPresenter(private val router: Router) : MvpPresenter<ViewMain>() {

    override fun onFirstViewAttach() {
        Log.d("TAG", "onFirstViewAttach() called")
        viewState.init()
        router.replaceScreen(FragmentSourcesScreen)
        super.onFirstViewAttach()
    }

    fun openScreenNews() {
        router.replaceScreen(FragmentViewPagerNewsScreen)
    }

    fun openScreenSources() {
        router.newRootScreen(FragmentSourcesScreen)
    }

    fun openScreenProfile() {
        router.newRootChain(FragmentProfileMainScreen)
    }

    fun onBackPressedRouter() {
        Log.d("Navigate", " router.exit() ActivityPresenter")
        router.exit()
    }
}