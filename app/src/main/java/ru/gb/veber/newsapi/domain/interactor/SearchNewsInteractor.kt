package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.domain.models.AccountModel
import ru.gb.veber.newsapi.domain.models.AccountSourcesModel
import ru.gb.veber.newsapi.domain.models.ArticleModel
import ru.gb.veber.newsapi.domain.models.SourcesModel
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.AccountSourcesRepo
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import ru.gb.veber.newsapi.domain.repository.NewsRepo
import ru.gb.veber.newsapi.domain.repository.SourcesRepo
import javax.inject.Inject

class SearchNewsInteractor @Inject constructor(
    private val accountRepo: AccountRepo,
    private val sourcesRepo: SourcesRepo,
    private val accountSourcesRepo: AccountSourcesRepo,
    private val articleRepo: ArticleRepo,
    private val newsRepo: NewsRepo,
) {
    suspend fun getAccount(accountId: Int): AccountModel {
        return accountRepo.getAccountByIdV2(accountId)
    }

    suspend fun getSources(): List<SourcesModel> {
        return sourcesRepo.getSources()
    }

    suspend fun getLikeSources(accountId: Int): List<SourcesModel> {
        return accountSourcesRepo.getLikeSources(accountId)
    }

    suspend fun getArticles(accountId: Int): List<ArticleModel> {
        return articleRepo.getArticleByIdV2(accountId)
    }

    suspend fun insertSource(accountSourcesModel: AccountSourcesModel) {
        accountSourcesRepo.insert(accountSourcesModel)
    }

    suspend fun insertArticle(articleModel: ArticleModel, accountId: Int) {
        articleRepo.insertArticle(articleModel, accountId)
    }

    suspend fun deleteArticleByIdFavorites(toString: String, accountId: Int) {
        articleRepo.deleteArticleByIdFavorites(toString, accountId)
    }

    suspend fun getNews(
        sources: String?,
        q: String?,
        searchIn: String?,
        sortBy: String?,
        from: String?,
        to: String?,
        key: String,
    ): List<ArticleModel> = newsRepo.getEverythingKeyWordSearchInSourcesV2(
        sources,
        q,
        searchIn,
        sortBy,
        from,
        to,
        key
    ).articles
}
