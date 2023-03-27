package ru.gb.veber.newsapi.domain.repository

import ru.gb.veber.newsapi.domain.models.Article

interface ArticleRepo {
    suspend fun insertArticleV2(article: Article, accountId: Int)
    suspend fun updateArticleV2(article: Article, accountId: Int)
    suspend fun deleteArticleV2(article: Article, accountId: Int)
    suspend fun deleteAllArticleV2()
    suspend fun getHistoryArticleByIdV2(accountId: Int): List<Article>
    suspend fun getLikeArticleByIdV2(accountId: Int): List<Article>
    suspend fun getLastArticleV2(): Article
    suspend fun getArticleByIdV2(accountId: Int): List<Article>
    suspend fun deleteArticleIsFavoriteByIdV2(accountId: Int)
    suspend fun deleteArticleIsHistoryByIdV2(accountId: Int)
    suspend fun deleteArticleByIdFavoritesV2(title: String, accountId: Int)
    suspend fun deleteArticleByIdHistoryV2(title: String, accountId: Int)
    suspend fun deleteArticleByIdHistoryGroupV2( accountId: Int,dateAdded:String)
}
