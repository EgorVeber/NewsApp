package ru.gb.veber.newsapi.domain.models

import ru.gb.veber.ui_common.ALL_COUNTRY
import ru.gb.veber.ui_common.ALL_COUNTRY_VALUE

data class AccountModel(
    val id: Int,
    var userName: String,
    var email: String,
    var password: String,
    val createdAt: String,
    var totalHistory: String? = "0",
    var totalFavorites: String? = "0",
    var totalSources: String? = "0",
    var saveHistory: Boolean = true,
    var saveSelectHistory: Boolean = true,
    var displayOnlySources: Boolean = false,
    var myCountry: String = ALL_COUNTRY,
    var countryCode: String = ALL_COUNTRY_VALUE,
)