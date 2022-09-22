package ru.gb.veber.newsapi.view.newswebview

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface FragmentWebView : MvpView {
    fun init(url: String)
}
