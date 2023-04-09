package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.models.room.entity.HistorySelectDbEntity
import ru.gb.veber.newsapi.domain.models.HistorySelect

fun HistorySelect.toHistorySelectDbEntity(): HistorySelectDbEntity 
    = HistorySelectDbEntity(
        id = id,
        accountID = accountID,
        keyWord = keyWord.toString(),
        searchIn = searchIn.toString(),
        sortByKeyWord = sortByKeyWord.toString(),
        sortBySources = sortBySources.toString(),
        sourcesId = sourcesId.toString(),
        dateSources = dateSources.toString(),
        sourcesName = sourcesName.toString()
    )
