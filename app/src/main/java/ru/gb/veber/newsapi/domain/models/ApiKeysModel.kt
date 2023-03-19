package ru.gb.veber.newsapi.domain.models

data class ApiKeysModel(
    val id: Int = 0,
    val accountId: Int,
    var keyApi: String,
    var actived: Boolean,
    var firstRequest: String,
    var lastRequest: String,
    var countRequest: Int,
    var countMax: Int,
)
