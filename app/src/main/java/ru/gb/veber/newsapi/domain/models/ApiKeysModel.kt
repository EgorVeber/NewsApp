package ru.gb.veber.newsapi.domain.models

import ru.gb.veber.newsapi.common.utils.CREATE_KEYS_COUNT

data class ApiKeysModel(
    val id: Int = 0,
    val accountId: Int,
    var keyApi: String,
    var actived: Boolean = false,
    var firstRequest: String,
    var lastRequest: String,
    var countRequest: String = CREATE_KEYS_COUNT,
    var countMax: String = CREATE_KEYS_COUNT,
)
