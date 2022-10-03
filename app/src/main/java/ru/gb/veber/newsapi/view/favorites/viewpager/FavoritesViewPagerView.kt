package ru.gb.veber.newsapi.view.favorites.viewpager

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface FavoritesViewPagerView : MvpView {
    fun init()
}
