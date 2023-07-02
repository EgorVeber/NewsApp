package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.database.entity.AccountEntity
import ru.gb.veber.newsapi.domain.models.AccountModel

fun AccountModel.toAccountDbEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        userName = userName,
        email = email,
        createdAt = createdAt,
        password = password,
        saveHistory = saveHistory,
        saveSelectHistory = saveSelectHistory,
        displayOnlySources = displayOnlySources,
        myCountry = myCountry,
        countryCode = countryCode
    )
}
