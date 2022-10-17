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
    fun setAlfa()

    fun setImageFilterButton()
     fun setCountry(map: List<String>)
    fun showCountryList()
    fun hideFilter()
    fun setAlfaCancel()
    fun setImageFilterButtonCancel()
    fun hideCountryList()
    fun showCancelButton()
    fun hideCancelButton()
}
