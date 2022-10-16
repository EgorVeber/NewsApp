package ru.gb.veber.newsapi.view.topnews.pageritem

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.gb.veber.newsapi.model.Article

@StateStrategyType(AddToEndSingleStrategy::class)
interface TopNewsView : MvpView {
    fun init()
    fun setSources(articles: List<Article>)
    fun clickNews(it: Article)
    fun showFilter()
    fun hideFilter()
    fun behaviorHide()
    fun visibilityFilterButton()
    fun hideFavorites()
    fun emptyList()
    fun changeNews(articleListHistory: MutableList<Article>)
    fun setLikeResourcesNegative()
    fun setLikeResourcesActive()

    @StateStrategyType(SkipStrategy::class)
    fun removeBadge()

    @StateStrategyType(SkipStrategy::class)
    fun addBadge()
    fun sheetExpanded()
    fun hideGroupArticle()
    fun showGroupArticle()
    fun hideViewPagerButton()
    fun showViewpagerButton()
    fun showGroupFilter()
    fun hideGroupFilter()
     fun setCountry(map: List<String>)
}
