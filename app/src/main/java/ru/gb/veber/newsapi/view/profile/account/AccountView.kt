package ru.gb.veber.newsapi.view.profile.account

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface AccountView : MvpView {
    fun init()
    fun logout()
}
