package ru.gb.veber.newsapi.domain.models

import ru.gb.veber.newsapi.common.utils.FOCUS_DEFAULT

data class SourcesModel(
    val id: Int,
    val idSources: String? = "",
    var name: String? = "",
    var description: String? = "",
    var url: String? = "",
    var category: String? = "",
    var language: String? = "",
    var country: String? = "",
    var liked: Boolean = false,
    var totalHistory: Int = 0,
    var totalFavorites: Int = 0,
    //TODO Убирается в задачи на рефакторинг экрана источников.
    var focusType: Int = FOCUS_DEFAULT
)