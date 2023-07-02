package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.database.entity.HistorySelectEntity
import ru.gb.veber.newsapi.domain.models.HistorySelectModel

fun HistorySelectEntity.toHistorySelect(): HistorySelectModel {
    return HistorySelectModel(
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
