package ru.gb.veber.newsapi.view.profile.account

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.gb.veber.newsapi.model.Account

@StateStrategyType(AddToEndSingleStrategy::class)
interface AccountView : MvpView {
    fun init()
    fun setAccountInfo(account: Account)
    fun loading()
    fun setBottomNavigationIcon()
    @StateStrategyType(SkipStrategy::class)
    fun showDialog()
}
