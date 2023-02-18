package ru.gb.veber.newsapi.utils.mapper

import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity

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
