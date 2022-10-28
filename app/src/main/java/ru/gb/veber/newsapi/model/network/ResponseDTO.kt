package ru.gb.veber.newsapi.model.network

data class ArticlesDTO(
    val articles: List<ArticleDTO>,
    val status: String,
    val totalResults: Int,
)

data class ArticleDTO(
    var author: String? = "",
    val content: String,
    val description: String?,
    var publishedAt: String,
    val source: SourceDTO,
    val title: String,
    val url: String,
    val urlToImage: String?,
)

data class SourceDTO(
    var id: String? = "",
    val name: String,
)


data class SourcesRequestDTO(
    var status: String? = null,
    var sources: List<SourcesDTO> = listOf(),
)

data class SourcesDTO(
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    var url: String? = null,
    var category: String? = null,
    var language: String? = null,
    var country: String? = null,
)