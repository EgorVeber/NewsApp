package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.database.entity.ApiKeysEntity
import ru.gb.veber.newsapi.domain.models.ApiKeysModel

fun ApiKeysModel.toApiKeysEntity() =
    ApiKeysEntity(
        id = id,
        accountID = accountId,
        keyApi = keyApi,
        actived = if (actived) 1 else 0,
        firstRequest = firstRequest,
        lastRequest = lastRequest,
        countRequest = countRequest.toInt(),
        countMax = countMax.toInt()
    )

fun ApiKeysEntity.toApiKeysModel() =
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