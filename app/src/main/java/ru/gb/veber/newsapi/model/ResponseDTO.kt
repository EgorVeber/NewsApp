package ru.gb.veber.newsapi.model

import com.google.gson.annotations.SerializedName

data class ArticlesDTO(
    val articles: List<ArticleDTO>,
    val status: String,
    val totalResults: Int,
)

data class ArticleDTO(
    var author: String? = "Аноним",
    val content: String,
    val description: String?,
    var publishedAt: String,
    val source: SourceDTO,
    val title: String,
    val url: String,
    val urlToImage: String?,
)

data class SourceDTO(
    var id: String? = "Не проверенный источник",
    val name: String,
)


data class SourcesRequestDTO(
    @SerializedName("status") var status: String? = null,
    @SerializedName("sources") var sources: List<SourcesDTO> = listOf(),
)

data class SourcesDTO(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("category") var category: String? = null,
    @SerializedName("language") var language: String? = null,
    @SerializedName("country") var country: String? = null,
)