package ru.gb.veber.newsapi.view.profile.account.settings.customize

object CategoryData {

    fun getCategory() = mutableListOf(
        Category("General"),
        Category("Business"),
        Category("Entertainment"),
        Category("Health"),
        Category("Science"),
        Category("Sports"),
        Category("Technology"),
    )
}