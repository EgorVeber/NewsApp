package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.models.room.entity.AccountDbEntity
import ru.gb.veber.newsapi.domain.models.Account

fun Account.toAccountDbEntity(): AccountDbEntity {
    return AccountDbEntity(
        id = this.id,
        userName = this.userName,
        email = this.email,
        createdAt = this.createdAt,
        password = this.password,
        saveHistory = this.saveHistory,
        saveSelectHistory = this.saveSelectHistory,
        displayOnlySources = this.displayOnlySources,
        myCountry = this.myCountry,
        countryCode = this.countryCode
    )
}
