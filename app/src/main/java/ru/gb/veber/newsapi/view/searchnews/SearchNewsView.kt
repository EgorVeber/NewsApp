package ru.gb.veber.newsapi.view.searchnews

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.gb.veber.newsapi.model.Sources

@StateStrategyType(AddToEndSingleStrategy::class)
interface SearchNewsView : MvpView {
    fun init()
     fun setSources(map: List<Sources>)
}