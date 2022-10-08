package ru.gb.veber.newsapi.view.searchnews

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.gb.veber.newsapi.model.Sources

@StateStrategyType(AddToEndSingleStrategy::class)
interface SearchNewsView : MvpView {
     fun setSources(map: List<Sources>)
     fun hideSelectHistory()
    fun updateAdapter(likeSources: List<Sources>)
}