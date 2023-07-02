package ru.gb.veber.newsapi.presentation.search.recycler

import ru.gb.veber.newsapi.domain.models.HistorySelectModel

interface RecyclerListenerHistorySelect {
    fun clickHistoryItem(historySelectModel: HistorySelectModel)
    fun deleteHistoryItem(historySelectModel: HistorySelectModel)
}