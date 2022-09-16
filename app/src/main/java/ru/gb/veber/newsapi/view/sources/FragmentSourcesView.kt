package ru.gb.veber.newsapi.view.sources

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.gb.veber.newsapi.model.SourcesDTO

@StateStrategyType(AddToEndSingleStrategy::class)
interface FragmentSourcesView : MvpView {
    fun init()
    fun setSources(list: List<SourcesDTO>)
}
