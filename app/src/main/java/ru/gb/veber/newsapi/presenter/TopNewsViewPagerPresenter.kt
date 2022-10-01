package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerView

class TopNewsViewPagerPresenter(private val router: Router) :
    MvpPresenter<TopNewsViewPagerView>() {
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