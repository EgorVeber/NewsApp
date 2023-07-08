package ru.gb.veber.newsapi.domain.models

data class SourcesBaseModel(
    var status: String? = null,
    var sources: List<SourcesModel> = listOf(),
)