package ru.gb.veber.newsapi.domain.models

data class Sources(
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
)