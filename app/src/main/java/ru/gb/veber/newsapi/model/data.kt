package ru.gb.veber.newsapi.model

data class Article(
    var id:Int?=0,
    var author: String?,
    var description: String?,
    var publishedAt: String,
    var publishedAtChange: String?,
    var source: Source,
    var title: String,
    var url: String,
    var urlToImage: String?,
    var viewType: Int=0,
    var isHistory:Boolean=false,
    var isFavorites:Boolean=false
)

data class Source(
    var id: String?,
    var name: String,
)

data class Account(
    val id: Int,
    var userName: String,
    var email: String,
    var password: String,
    val createdAt: String,
    var totalHistory: String?="0",
    var totalFavorites: String?="0",
    var saveHistory: Boolean=true,
)
