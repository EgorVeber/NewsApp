package ru.gb.veber.newsapi.domain.models

data class SourceModel(
    var id: String,
    var name: String,
) {
    companion object {
        val empty = SourceModel("0", "")
    }
}