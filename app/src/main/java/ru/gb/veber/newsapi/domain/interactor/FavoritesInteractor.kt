package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.domain.models.Article
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import javax.inject.Inject

class FavoritesInteractor @Inject constructor(
    private val articleRepoImpl: ArticleRepo
) {

    suspend fun getLikeArticle(accountId: Int): List<Article> {
        return articleRepoImpl.getLikeArticleByIdV2(accountId)
    }

    suspend fun getHistoryArticle(accountId: Int): List<Article> {
        return articleRepoImpl.getHistoryArticleByIdV2(accountId)
    }

    suspend fun deleteArticleFavorites(title: String, accountId: Int) {
        articleRepoImpl.deleteArticleByIdFavoritesV2(title, accountId)
    }

    suspend fun deleteArticleHistory(title: String, accountId: Int) {
        articleRepoImpl.deleteArticleByIdHistoryV2(title, accountId)
    }

    suspend fun deleteArticleHistoryGroup(accountId: Int,dateAdded:String) {
        articleRepoImpl.deleteArticleByIdHistoryGroupV2(accountId, dateAdded)
    }
}