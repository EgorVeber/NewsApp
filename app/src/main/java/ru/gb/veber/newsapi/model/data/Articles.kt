package ru.gb.veber.newsapi.model.data

import com.google.gson.annotations.SerializedName

data class Articles(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int,
)

data class Article(
    val author: String?="Аноним",
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String,
)

data class Source(
    val id: String?="Не проверенный источник",
    val name: String,
)


data class SourcesRequest(
    @SerializedName("status") var status: String? = null,
    @SerializedName("sources") var sources: ArrayList<Sources> = arrayListOf(),
)

data class Sources(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("category") var category: String? = null,
    @SerializedName("language") var language: String? = null,
    @SerializedName("country") var country: String? = null,
)