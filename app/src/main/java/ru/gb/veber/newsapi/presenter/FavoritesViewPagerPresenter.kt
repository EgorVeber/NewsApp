package ru.gb.veber.newsapi.presenter

import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerView
import javax.inject.Inject

class FavoritesViewPagerPresenter() :
    MvpPresenter<FavoritesViewPagerView>() {

    @Inject
    lateinit var router: Router
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }
}