package ru.gb.veber.newsapi.domain.models


data class ApiKeysModel(
    val id: Int = 0,
    val accountId: Int,
    var keyApi: String,
    var actived: Boolean = false,
    var firstRequest: String,
    var lastRequest: String,
    var countRequest: String = CREATE_KEYS_COUNT,
    var countMax: String = CREATE_KEYS_COUNT,
) {
    companion object {
        private const val CREATE_KEYS_COUNT = "100"
    }
}
