package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.view.viewpagernews.FragmentViewPagerNewsView

class FragmentViewPagerNewsPresenter(private val router: Router) :
    MvpPresenter<FragmentViewPagerNewsView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        Log.d("Back", "onBackPressedRouter() News")
        return true
    }
}