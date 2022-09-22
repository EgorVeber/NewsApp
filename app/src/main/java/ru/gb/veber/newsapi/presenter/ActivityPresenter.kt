package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.view.activity.ViewMain


class ActivityPresenter(private val router: Router) : MvpPresenter<ViewMain>() {

    override fun onFirstViewAttach() {
        viewState.init()
        router.replaceScreen(FragmentViewPagerNewsScreen)
        super.onFirstViewAttach()
    }

    fun openScreenNews() {
        router.navigateTo(FragmentViewPagerNewsScreen)
    }

    fun openScreenSources() {
        router.navigateTo(FragmentSourcesScreen)
    }

    fun openScreenProfile() {
        router.navigateTo(FragmentProfileScreen)
    }

    fun onBackPressedRouter() {
        Log.d("Back", "onBackPressedRouter() ActivityPresenter")
        router.exit()
    }
}