package ru.gb.veber.newsapi.view.news

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.gb.veber.newsapi.model.data.ArticleDTO

@StateStrategyType(AddToEndSingleStrategy::class)
interface FragmentNewsView : MvpView {
    fun init()
    fun setSources(articles: List<ArticleDTO>)
}
