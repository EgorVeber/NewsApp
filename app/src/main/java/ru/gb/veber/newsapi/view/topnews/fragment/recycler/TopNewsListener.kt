package ru.gb.veber.newsapi.view.topnews.fragment.recycler

import ru.gb.veber.newsapi.model.Article

fun interface TopNewsListener {
    fun clickNews(article: Article)
    fun deleteFavorites(article: Article) {}
    fun deleteHistory(article: Article) {}
    fun clickGroupHistory(article: Article) {}
    fun deleteGroupHistory(article: Article) {}
}