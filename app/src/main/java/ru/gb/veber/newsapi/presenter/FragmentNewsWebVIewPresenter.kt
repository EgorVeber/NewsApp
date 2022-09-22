package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.view.newswebview.FragmentWebView

class FragmentNewsWebVIewPresenter(private val router: Router,private val url: String) :
    MvpPresenter<FragmentWebView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init(url)
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        Log.d("Back", "onBackPressedRouter() FragmentNewsWebVIewPresenter")
        return true
    }
}