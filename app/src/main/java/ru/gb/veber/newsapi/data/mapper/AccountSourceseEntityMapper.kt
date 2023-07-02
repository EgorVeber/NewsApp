package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.database.entity.AccountSourcesEntity
import ru.gb.veber.newsapi.domain.models.AccountSourcesModel

fun AccountSourcesModel.toAccountSourcesEntity(): AccountSourcesEntity =
    AccountSourcesEntity(accountId = accountId, sourcesId = sourcesId)