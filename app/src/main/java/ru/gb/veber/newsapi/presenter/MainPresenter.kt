package ru.gb.veber.newsapi.presenter

import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter


class MainPresenter(private val router: Router) : MvpPresenter<ViewMain>() {

    override fun onFirstViewAttach() {
        viewState.init()
        router.replaceScreen(FragmentSourcesScreen)
        super.onFirstViewAttach()
    }
}