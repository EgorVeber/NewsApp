package ru.gb.veber.newsapi.domain.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.data.models.room.entity.ArticleDbEntity

interface ArticleRepo {
    fun insertArticle(articleDbEntity: ArticleDbEntity): Completable
    fun updateArticle(articleDbEntity: ArticleDbEntity): Completable
    fun deleteArticle(articleDbEntity: ArticleDbEntity): Completable
    fun deleteAllArticle(): Completable
    fun getHistoryArticleById(accountId: Int): Single<List<ArticleDbEntity>>
    fun getLikeArticleById(accountId: Int): Single<List<ArticleDbEntity>>
    fun getLastArticle(): Single<ArticleDbEntity>
    fun getArticleById(accountId: Int): Single<List<ArticleDbEntity>>
    fun deleteArticleIsFavoriteById(accountId: Int): Completable
    fun deleteArticleIsHistoryById(accountId: Int): Completable
    fun deleteArticleByIdFavorites(title: String, accountId: Int): Completable
    fun deleteArticleByIdHistory(title: String, accountId: Int): Completable
    fun deleteArticleByIdHistoryGroup( accountId: Int,dateAdded:String): Completable


    suspend fun insertArticleV2(articleDbEntity: ArticleDbEntity)
    suspend fun updateArticleV2(articleDbEntity: ArticleDbEntity)
    suspend fun deleteArticleV2(articleDbEntity: ArticleDbEntity)
    suspend fun deleteAllArticleV2()
    suspend fun getHistoryArticleByIdV2(accountId: Int): List<ArticleDbEntity>
    suspend fun getLikeArticleByIdV2(accountId: Int): List<ArticleDbEntity>
    suspend fun getLastArticleV2(): ArticleDbEntity
    suspend fun getArticleByIdV2(accountId: Int): List<ArticleDbEntity>
    suspend fun deleteArticleIsFavoriteByIdV2(accountId: Int)
    suspend fun deleteArticleIsHistoryByIdV2(accountId: Int)
    suspend fun deleteArticleByIdFavoritesV2(title: String, accountId: Int)
    suspend fun deleteArticleByIdHistoryV2(title: String, accountId: Int)
    suspend fun deleteArticleByIdHistoryGroupV2( accountId: Int,dateAdded:String)
}
