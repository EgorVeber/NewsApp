package ru.gb.veber.newsapi.view.webview

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface WebView : MvpView {
    fun init(url: String)
}
