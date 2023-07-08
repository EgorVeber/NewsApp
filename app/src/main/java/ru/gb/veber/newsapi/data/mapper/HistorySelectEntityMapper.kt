package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.database.entity.HistorySelectEntity
import ru.gb.veber.newsapi.domain.models.HistorySelectModel

fun HistorySelectModel.toHistorySelectEntity(): HistorySelectEntity
    = HistorySelectEntity(
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
