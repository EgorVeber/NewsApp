package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerView

class FavoritesViewPagerPresenter(private val router: Router) :
    MvpPresenter<FavoritesViewPagerView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        Log.d("Back", "onBackPressedRouter() TopNewsViewPagerPresenter")
        return true
    }
}