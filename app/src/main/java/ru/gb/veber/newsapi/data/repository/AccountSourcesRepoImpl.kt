package ru.gb.veber.newsapi.data.repository

import ru.gb.veber.newsapi.data.database.dao.AccountSourcesDao
import ru.gb.veber.newsapi.data.mapper.toAccountSourcesEntity
import ru.gb.veber.newsapi.data.mapper.toSources
import ru.gb.veber.newsapi.domain.models.AccountSourcesModel
import ru.gb.veber.newsapi.domain.models.SourcesModel
import ru.gb.veber.newsapi.domain.repository.AccountSourcesRepo

class AccountSourcesRepoImpl(private val accountSourcesDao: AccountSourcesDao) :
    AccountSourcesRepo {
    override suspend fun insert(accountSourcesModel: AccountSourcesModel) {
        accountSourcesDao.insert(accountSourcesModel.toAccountSourcesEntity())
    }

    override suspend fun getLikeSources(accountId: Int): List<SourcesModel> {
        return accountSourcesDao.getLikeSourcesFromAccount(accountId).map { sourcesDb ->
            sourcesDb.toSources().also { source -> source.liked = true }
        }
    }

    override suspend fun deleteSourcesLikeV2(accountId: Int, sourcesId: Int) {
        accountSourcesDao.deleteSourcesLikeV2(accountId, sourcesId)
    }

    override suspend fun deleteSourcesV2(accountId: Int) {
        accountSourcesDao.deleteSourcesV2(accountId)
    }
}
