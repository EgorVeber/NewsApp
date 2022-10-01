package ru.gb.veber.newsapi.view.topnews.viewpager

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface TopNewsViewPagerView : MvpView {
    fun init()
}
