package ru.gb.veber.newsapi.model.repository.room

import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity
import ru.gb.veber.newsapi.model.database.entity.ArticleDbEntity

interface ArticleRepo {
    fun insertArticle(articleDbEntity: ArticleDbEntity): Completable
    fun updateArticle(articleDbEntity: ArticleDbEntity): Completable
    fun deleteArticle(articleDbEntity: ArticleDbEntity): Completable
    fun deleteAllArticle(): Completable
    fun getArticleById(accountId: Int): Single<List<ArticleDbEntity>>
}