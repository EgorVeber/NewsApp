package ru.gb.veber.newsapi.view.allnews

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.ArticleDTO

@StateStrategyType(AddToEndSingleStrategy::class)
interface AllNewsView:MvpView {
    fun init()
    fun setNews(articles: List<Article>)
    fun loading()
}