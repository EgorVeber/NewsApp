package ru.gb.veber.newsapi.model.data

import com.google.gson.annotations.SerializedName

data class ArticlesDTO(
    val articles: List<ArticleDTO>,
    val status: String,
    val totalResults: Int,
)

data class ArticleDTO(
    var author: String? = "Аноним",
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: SourceDTO,
    val title: String,
    val url: String,
    val urlToImage: String,
) {
    fun editRequest(): ArticleDTO {
        if (this.source.id == null) {
            this.source.id = "Не проверенный источник"
        }
        if (this.author == null) {
            this.author = "Аноним"
        }
        return this
    }
}

data class SourceDTO(
    var id: String? = "Не проверенный источник",
    val name: String,
)


data class SourcesRequestDTO(
    @SerializedName("status") var status: String? = null,
    @SerializedName("sources") var sources: ArrayList<SourcesDTO> = arrayListOf(),
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