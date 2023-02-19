package ru.gb.veber.newsapi.view.sources.recycler

import ru.gb.veber.newsapi.model.Sources

interface SourcesListener {
    fun openUrl(url: String?)
    fun imageClick(source: Sources)
    fun newsClick(source: String?, name: String?)
}