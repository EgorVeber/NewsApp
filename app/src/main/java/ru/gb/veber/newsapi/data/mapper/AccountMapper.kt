package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.database.entity.AccountEntity
import ru.gb.veber.newsapi.domain.models.AccountModel

fun AccountEntity.toAccount(): AccountModel {
    return AccountModel(
        id = id,
        userName = userName,
        email = email,
        createdAt = createdAt,
        password = password,
        saveHistory = saveHistory,
        displayOnlySources = displayOnlySources,
        saveSelectHistory = saveSelectHistory
    )
}
