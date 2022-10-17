package ru.gb.veber.newsapi.presenter

import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.view.webview.WebView

class WebViewPresenter(private val router: Router) :
    MvpPresenter<WebView>() {

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun back() {
        router.exit()
    }


    fun successLoading() {
        viewState.showPage()
    }
}