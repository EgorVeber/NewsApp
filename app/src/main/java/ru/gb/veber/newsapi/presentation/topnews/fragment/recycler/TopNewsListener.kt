package ru.gb.veber.newsapi.presentation.topnews.fragment.recycler

import ru.gb.veber.newsapi.presentation.models.ArticleUiModel

fun interface TopNewsListener {
    fun clickNews(articleUiModel: ArticleUiModel)
    fun deleteFavorites(articleUiModel: ArticleUiModel) {}
    fun deleteHistory(articleUiModel: ArticleUiModel) {}
    fun clickGroupHistory(articleUiModel: ArticleUiModel) {}
    fun deleteGroupHistory(articleUiModel: ArticleUiModel) {}
}
