package ru.gb.veber.newsapi.presentation.models

import ru.gb.veber.newsapi.presentation.favorites.delegate.ListItem

data class ArticleHistoryHeader(
    val dateAdded: String,
    val sizeArticle: String,
    val title: String,
) : ListItem