package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.models.room.entity.ApiKeysDbEntity
import ru.gb.veber.newsapi.domain.models.ApiKeysModel

fun ApiKeysModel.toApiKeysEntity() =
    ApiKeysDbEntity(
        id = id,
        accountID = accountId,
        keyApi = keyApi,
        actived = if (actived) 1 else 0,
        firstRequest = firstRequest,
        lastRequest = lastRequest,
        countRequest = countRequest.toInt(),
        countMax = countMax.toInt()
    )

fun ApiKeysDbEntity.toApiKeysModel() =
    ApiKeysModel(
        id = id,
        accountId = accountID,
        keyApi = keyApi,
        actived = actived == 1,
        firstRequest = firstRequest,
        lastRequest = lastRequest,
        countRequest = countRequest.toString(),
        countMax = countMax.toString()
    )