package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.data.models.network.ArticlesDTO
import ru.gb.veber.newsapi.data.models.room.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.ArticleDbEntity
import ru.gb.veber.newsapi.domain.models.Account
import ru.gb.veber.newsapi.domain.models.Sources
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
    suspend fun getAccount(accountId: Int): Account {
        return accountRepo.getAccountByIdV2(accountId)
    }

    suspend fun getSources(): List<Sources> {
        return sourcesRepo.getSourcesV2()
    }

    suspend fun getLikeSources(accountId: Int): List<Sources> {
        return accountSourcesRepo.getLikeSourcesFromAccountV2(accountId)
    }

    suspend fun getArticles(accountId: Int): List<ArticleDbEntity> {
        return articleRepo.getArticleByIdV2(accountId)
    }

    suspend fun insertSource(accountSourcesDbEntity: AccountSourcesDbEntity) {
        accountSourcesRepo.insertV2(accountSourcesDbEntity)
    }

    suspend fun insertArticle(toArticleDbEntity: ArticleDbEntity) {
        articleRepo.insertArticleV2(toArticleDbEntity)
    }

    suspend fun deleteArticleByIdFavoritesV2(toString: String, accountId: Int) {
        articleRepo.deleteArticleByIdFavoritesV2(toString, accountId)
    }

    suspend fun getNews(
        sources: String?,
        q: String?,
        searchIn: String?,
        sortBy: String?,
        from: String?,
        to: String?,
        key: String,
    ): ArticlesDTO {
        return newsRepo.getEverythingKeyWordSearchInSourcesV2(
            sources,
            q,
            searchIn,
            sortBy,
            from,
            to,
            key)
    }
}
