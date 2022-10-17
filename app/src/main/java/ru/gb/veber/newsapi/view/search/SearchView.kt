package ru.gb.veber.newsapi.view.search

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.gb.veber.newsapi.model.HistorySelect
import ru.gb.veber.newsapi.model.Sources

@StateStrategyType(AddToEndSingleStrategy::class)
interface SearchView : MvpView {
    fun setSources(map: List<Sources>)
    fun updateAdapter(likeSources: List<Sources>)
    fun searchInShow()
    fun sourcesInShow()

    @StateStrategyType(SkipStrategy::class)
    fun selectSources()

    @StateStrategyType(SkipStrategy::class)
    fun errorDateInput()

    fun pikerPositive(l: Long)
    fun pikerNegative()
    fun setHistory(map: List<HistorySelect>)
    fun hideSelectHistory()
    @StateStrategyType(SkipStrategy::class)
    fun emptyHistory()
    fun hideEmptyList()
}