package ru.gb.veber.newsapi.view.search.searchnews

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.gb.veber.newsapi.model.Article

@StateStrategyType(AddToEndSingleStrategy::class)
interface AllNewsView:MvpView {
    fun setNews(articles: List<Article>)
    fun loading()
    fun clickNews(article: Article)
    fun hideFavorites()
    fun setTitle(keyWord: String?, sourcesId: String?, s: String?, dateSources: String?)
    fun emptyList()
    fun hideSaveSources()
    fun showSaveSources()
    @StateStrategyType(SkipStrategy::class)
    fun successSaveSources()
    fun sheetExpanded()
    fun setLikeResourcesNegative()
    fun setLikeResourcesActive()
    @StateStrategyType(SkipStrategy::class)
    fun removeBadge()
    @StateStrategyType(SkipStrategy::class)
    fun addBadge()
    fun changeNews(articleListHistory: MutableList<Article>)
}