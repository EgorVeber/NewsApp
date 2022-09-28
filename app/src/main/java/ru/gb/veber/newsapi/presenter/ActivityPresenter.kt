package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.view.activity.ViewMain


class ActivityPresenter(private val router: Router) : MvpPresenter<ViewMain>() {

    override fun onFirstViewAttach() {
        Log.d("TAG", "onFirstViewAttach() called")
        viewState.init()
        router.replaceScreen(FragmentProfileMainScreen)
        super.onFirstViewAttach()
    }

    fun openScreenNews() {
        router.navigateTo(FragmentViewPagerNewsScreen)
    }

    fun openScreenSources() {
        router.navigateTo(FragmentSourcesScreen)
    }

    fun openScreenProfile() {
        router.navigateTo(FragmentProfileMainScreen)
    }

    fun onBackPressedRouter() {
        Log.d("@@@", "onBackPressedRouter() ActivityPresenter")
        router.exit()
    }
}