package ru.gb.veber.newsapi.presentation.activity

interface EventLogoutAccountScreen {
    fun bottomNavigationSetDefaultIcon()
    fun bottomNavigationSetCurrentAccount(checkLogin: String)
    fun bottomNavigationSetTitleCurrentAccount(checkLogin: String)
}
