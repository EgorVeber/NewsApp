package ru.gb.veber.newsapi.view.activity

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface ViewMain : MvpView {
    fun init()
    fun onCreateSetIconTitleAccount(accountLogin: String)
    fun completableInsertSources()
}
