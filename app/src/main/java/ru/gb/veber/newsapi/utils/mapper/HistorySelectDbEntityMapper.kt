package ru.gb.veber.newsapi.utils.mapper

import ru.gb.veber.newsapi.model.HistorySelect
import ru.gb.veber.newsapi.model.database.entity.HistorySelectDbEntity

fun HistorySelect.toHistorySelectDbEntity(): HistorySelectDbEntity {
    return HistorySelectDbEntity(
        id = this.id,
        accountID = this.accountID,
        keyWord = this.keyWord.toString(),
        searchIn = this.searchIn.toString(),
        sortByKeyWord = this.sortByKeyWord.toString(),
        sortBySources = this.sortBySources.toString(),
        sourcesId = this.sourcesId.toString(),
        dateSources = this.dateSources.toString(),
        sourcesName = this.sourcesName.toString()
    )
}