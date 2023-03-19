package ru.gb.veber.newsapi.presentation.search.recycler

import ru.gb.veber.newsapi.domain.models.HistorySelect

interface RecyclerListenerHistorySelect {
    fun clickHistoryItem(historySelect: HistorySelect)
    fun deleteHistoryItem(historySelect: HistorySelect)
}