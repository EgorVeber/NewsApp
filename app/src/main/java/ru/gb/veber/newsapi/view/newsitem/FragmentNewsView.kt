package ru.gb.veber.newsapi.view.newsitem

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.gb.veber.newsapi.model.Article

@StateStrategyType(AddToEndSingleStrategy::class)
interface FragmentNewsView : MvpView {
    fun init()
    fun setSources(articles: List<Article>)
    fun clickNews(it: Article)
    fun showFilter()
    fun hideFilter()
    fun behaviorHide()
    fun visibilityFilterButton()
}
