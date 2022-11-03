package ru.gb.veber.newsapi.model.repository.room

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.database.entity.ArticleDbEntity

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
}