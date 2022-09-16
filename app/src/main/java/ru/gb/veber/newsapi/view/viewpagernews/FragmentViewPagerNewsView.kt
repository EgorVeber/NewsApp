package ru.gb.veber.newsapi.view.viewpagernews

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface FragmentViewPagerNewsView : MvpView {
    fun init()
}
