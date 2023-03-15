package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.data.models.room.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.ArticleDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.HistorySelectDbEntity
import ru.gb.veber.newsapi.domain.models.Sources
import ru.gb.veber.newsapi.domain.repository.AccountSourcesRepo
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import ru.gb.veber.newsapi.domain.repository.HistorySelectRepo
import ru.gb.veber.newsapi.domain.repository.SourcesRepo
import javax.inject.Inject

class SourceInteractor
@Inject constructor(
    private val accountSourcesRepoImpl: AccountSourcesRepo,
    private val sourcesRepoImpl: SourcesRepo,
    private val articleRepoImpl: ArticleRepo,
    private val historySelectRepoImpl: HistorySelectRepo
) {
    suspend fun getSourcesV2(): MutableList<Sources> {
        return sourcesRepoImpl.getSourcesV2()
    }

    suspend fun getLikeSourcesFromAccountV2(accountId: Int): List<Sources> {
        return accountSourcesRepoImpl.getLikeSourcesFromAccountV2(accountId)
    }

    suspend fun getArticleByIdV2(accountId: Int): List<ArticleDbEntity> {
        return articleRepoImpl.getArticleByIdV2(accountId)
    }

    suspend fun deleteSourcesLikeV2(accountId: Int, sourcesId: Int) {
        accountSourcesRepoImpl.deleteSourcesLikeV2(accountId, sourcesId)
    }

    suspend fun insertV2(
        accountSourcesDbEntity: AccountSourcesDbEntity) {
        accountSourcesRepoImpl.insertV2(accountSourcesDbEntity)
    }

    suspend fun insertSelectV2(historyDbEntity: HistorySelectDbEntity){
            historySelectRepoImpl.insertSelectV2(historyDbEntity)
    }
}