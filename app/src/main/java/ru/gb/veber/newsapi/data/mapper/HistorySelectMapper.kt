package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.model.HistorySelect
import ru.gb.veber.newsapi.data.models.room.entity.HistorySelectDbEntity

fun HistorySelectDbEntity.toHistorySelect(): HistorySelect {
    return HistorySelect(
        id = this.id,
        accountID = this.accountID,
        keyWord = this.keyWord,
        searchIn = this.searchIn,
        sortByKeyWord = this.sortByKeyWord,
        sortBySources = this.sortBySources,
        sourcesId = this.sourcesId,
        dateSources = this.dateSources,
        sourcesName = this.sourcesName
    )
}
