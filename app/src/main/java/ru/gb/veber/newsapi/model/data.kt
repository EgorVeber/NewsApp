package ru.gb.veber.newsapi.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class Article(
    var id: Int? = 0,
    var author: String?,
    var description: String?,
    var publishedAt: String,
    var publishedAtChange: String?,
    var source: Source,
    var title: String,
    var url: String,
    var urlToImage: String?,
    var viewType: Int = 0,
    var isHistory: Boolean = false,
    var isFavorites: Boolean = false,
    var dateAdded: String? = null,
)

data class Sources(
    val id: Int,
    val idSources: String? = "",
    var name: String? = "",
    var description: String? = "",
    var url: String? = "",
    var category: String? = "",
    var language: String? = "",
    var country: String? = "",
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
    var totalHistory: String? = "0",
    var totalFavorites: String? = "0",
    var saveHistory: Boolean = true,
)
