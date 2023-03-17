package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.models.room.entity.ApiKeysDbEntity
import ru.gb.veber.newsapi.domain.models.ApiKeysModel

fun ApiKeysModel.toApiKeysEntity() =
    ApiKeysDbEntity(
        id = id,
        accountID = accountId,
        keyApi = keyApi,
        actived = actived,
        firstRequest = firstRequest,
        lastRequest = lastRequest,
        countRequest = countRequest,
        countMax = countMax
    )

fun ApiKeysDbEntity.toApiKeysModel() =
    ApiKeysModel(
        id = id,
        accountId = accountID,
        keyApi = keyApi,
        actived = actived,
        firstRequest = firstRequest,
        lastRequest = lastRequest,
        countRequest = countRequest,
        countMax = countMax
    )