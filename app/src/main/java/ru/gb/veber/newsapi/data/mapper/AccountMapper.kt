package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.models.room.entity.AccountDbEntity
import ru.gb.veber.newsapi.domain.models.Account

fun AccountDbEntity.toAccount(): Account {
    return Account(
        id = this.id,
        userName = this.userName,
        email = this.email,
        createdAt = this.createdAt,
        password = this.password,
        saveHistory = this.saveHistory,
        displayOnlySources = this.displayOnlySources,
        saveSelectHistory = this.saveSelectHistory
    )
}
