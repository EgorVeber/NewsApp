package ru.gb.veber.newsapi.presentation.sources.recycler

import ru.gb.veber.newsapi.domain.models.SourcesModel

interface SourcesListener {
    fun openUrl(url: String?)
    fun imageClick(source: SourcesModel)
    fun newsClick(source: String?, name: String?)
    fun focus(source: SourcesModel, type: Int)
}