package ru.gb.veber.newsapi.presenter

import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerView
import javax.inject.Inject

class TopNewsViewPagerPresenter() :
    MvpPresenter<TopNewsViewPagerView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }
    @Inject
    lateinit var router: Router
    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }
}