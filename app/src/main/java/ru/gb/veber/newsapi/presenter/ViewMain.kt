package ru.gb.veber.newsapi.presenter

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface ViewMain : MvpView {
    fun init()
}
