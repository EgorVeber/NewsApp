package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.models.room.entity.HistorySelectDbEntity
import ru.gb.veber.newsapi.domain.models.HistorySelect

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
