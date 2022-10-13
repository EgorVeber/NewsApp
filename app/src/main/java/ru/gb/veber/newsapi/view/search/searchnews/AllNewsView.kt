package ru.gb.veber.newsapi.view.search.searchnews

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.gb.veber.newsapi.model.Article

@StateStrategyType(AddToEndSingleStrategy::class)
interface AllNewsView:MvpView {
    fun setNews(articles: List<Article>)
    fun loading()
    fun clickNews(article: Article)
    fun successInsertArticle()
    fun hideFavorites()
    fun setTitle(keyWord: String?, sourcesId: String?, s: String?, dateSources: String?)
    fun emptyList()
    fun hideSaveSources()
    fun showSaveSources()
    fun successSaveSources()
}