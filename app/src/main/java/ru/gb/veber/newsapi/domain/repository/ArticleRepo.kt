package ru.gb.veber.newsapi.domain.repository

import ru.gb.veber.newsapi.domain.models.ArticleModel

interface ArticleRepo {
    suspend fun insertArticle(articleModel: ArticleModel, accountId: Int)
    suspend fun updateArticleV2(articleModel: ArticleModel, accountId: Int)
    suspend fun deleteArticleV2(articleModel: ArticleModel, accountId: Int)
    suspend fun deleteAllArticleV2()
    suspend fun getHistoryArticleById(accountId: Int): List<ArticleModel>
    suspend fun getLikeArticleByIdV2(accountId: Int): List<ArticleModel>
    suspend fun getLastArticleV2(): ArticleModel
    suspend fun getArticleByIdV2(accountId: Int): List<ArticleModel>
    suspend fun deleteArticleIsFavoriteByIdV2(accountId: Int)
    suspend fun deleteArticleIsHistoryByIdV2(accountId: Int)
    suspend fun deleteArticleByIdFavorites(title: String, accountId: Int)
    suspend fun deleteArticleByIdHistory(title: String, accountId: Int)
    suspend fun deleteArticleByIdHistoryGroup(accountId: Int, dateAdded:String)
}
