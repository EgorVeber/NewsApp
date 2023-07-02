package ru.gb.veber.newsapi.data.models

class SourcesBaseResponse(
    var status: String? = null,
    var sources: List<SourcesResponse> = listOf(),
)