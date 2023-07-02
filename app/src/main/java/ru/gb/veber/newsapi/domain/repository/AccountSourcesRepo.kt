package ru.gb.veber.newsapi.domain.repository

import ru.gb.veber.newsapi.domain.models.AccountSourcesModel
import ru.gb.veber.newsapi.domain.models.SourcesModel

interface AccountSourcesRepo {
    suspend fun insert(accountSourcesModel: AccountSourcesModel)
    suspend fun getLikeSources(accountId: Int): List<SourcesModel>
    suspend fun deleteSourcesLikeV2(accountId: Int, sourcesId: Int)
    suspend fun deleteSourcesV2(accountId: Int)
}
