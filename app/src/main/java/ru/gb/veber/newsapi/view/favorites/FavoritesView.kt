package ru.gb.veber.newsapi.view.favorites

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.gb.veber.newsapi.model.Article

@StateStrategyType(AddToEndSingleStrategy::class)
interface FavoritesView : MvpView {
    fun init()
    fun setSources(list: List<Article>)
    fun loading()
    fun notAuthorized()
    fun emptyList()
    fun clickNews(it: Article)
}
