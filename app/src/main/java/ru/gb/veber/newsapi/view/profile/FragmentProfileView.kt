package ru.gb.veber.newsapi.view.profile

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface FragmentProfileView : MvpView {
    fun init()
    fun logout()
}
