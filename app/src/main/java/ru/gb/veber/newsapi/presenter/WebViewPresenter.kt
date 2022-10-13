package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.view.webview.WebView

class WebViewPresenter(private val router: Router) :
    MvpPresenter<WebView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        Log.d("Back", "onBackPressedRouter() WebViewPresenter")
        return true
    }

    fun back() {
        router.exit()
    }


    fun successLoading() {
        viewState.showPage()
    }
}