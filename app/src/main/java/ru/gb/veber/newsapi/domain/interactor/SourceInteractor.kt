package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.domain.models.AccountSourcesModel
import ru.gb.veber.newsapi.domain.models.ArticleModel
import ru.gb.veber.newsapi.domain.models.HistorySelectModel
import ru.gb.veber.newsapi.domain.models.SourcesModel
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
    private val historySelectRepoImpl: HistorySelectRepo,
) {
    suspend fun getSources(): MutableList<SourcesModel> {
        return sourcesRepoImpl.getSources().toMutableList()
    }

    suspend fun getLikeSourcesFromAccount(accountId: Int): List<SourcesModel> {
        return accountSourcesRepoImpl.getLikeSources(accountId)
    }

    suspend fun getArticleById(accountId: Int): List<ArticleModel> {
        return articleRepoImpl.getArticleByIdV2(accountId)
    }

    suspend fun deleteSourcesLike(accountId: Int, sourcesId: Int) {
        accountSourcesRepoImpl.deleteSourcesLikeV2(accountId, sourcesId)
    }

    suspend fun insert(accountSourcesModel: AccountSourcesModel) {
        accountSourcesRepoImpl.insert(accountSourcesModel)
    }

    suspend fun insertSelect(historySelectModel: HistorySelectModel) {
        historySelectRepoImpl.insertSelect(historySelectModel)
    }
}