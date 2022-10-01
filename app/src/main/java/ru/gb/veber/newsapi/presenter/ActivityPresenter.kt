package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.*
import ru.gb.veber.newsapi.model.getAccountID
import ru.gb.veber.newsapi.view.activity.ViewMain


class ActivityPresenter(private val router: Router) : MvpPresenter<ViewMain>() {

    override fun onFirstViewAttach() {
        Log.d("TAG", "onFirstViewAttach() called")
        viewState.init()
        super.onFirstViewAttach()
    }

    fun openScreenNews() {
        router.replaceScreen(TopNewsViewPagerScreen(getAccountID()))
    }

    fun openScreenSources() {
        router.replaceScreen(FragmentSourcesScreen)
    }

    fun openScreenProfile() {
        router.replaceScreen(FragmentProfileMainScreen(getAccountID()))
    }

    fun openScreenSearchNews() {
        router.replaceScreen(SearchNewsScreen(getAccountID()))
    }

    fun openScreenAllNews() {
        router.replaceScreen(AllNewsScreen(getAccountID()))
    }

    fun onBackPressedRouter() {
        Log.d("Navigate", " router.exit() ActivityPresenter")
        router.exit()
    }
}