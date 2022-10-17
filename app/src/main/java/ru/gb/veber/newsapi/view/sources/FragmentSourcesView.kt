package ru.gb.veber.newsapi.view.sources

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.gb.veber.newsapi.model.Sources

@StateStrategyType(AddToEndSingleStrategy::class)
interface FragmentSourcesView : MvpView {
    fun setSources(list: List<Sources>)
    fun setLogin()
}
