package ru.gb.veber.newsapi.model.repository.room

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.database.dao.AccountsDao
import ru.gb.veber.newsapi.model.database.dao.ArticleDao
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity
import ru.gb.veber.newsapi.model.database.entity.ArticleDbEntity
import ru.gb.veber.newsapi.utils.mapToAccount
import ru.gb.veber.newsapi.utils.subscribeDefault

class ArticleRepoImpl(private val articleDao: ArticleDao) : ArticleRepo {

    override fun insertArticle(articleDbEntity: ArticleDbEntity): Completable {
      return  articleDao.insertArticle(articleDbEntity).subscribeDefault()
    }

    override fun updateArticle(articleDbEntity: ArticleDbEntity): Completable {
       return articleDao.updateArticle(articleDbEntity).subscribeDefault()
    }

    override fun deleteArticle(articleDbEntity: ArticleDbEntity): Completable {
        return articleDao.deleteArticle(articleDbEntity).subscribeDefault()
    }

    override fun deleteAllArticle(): Completable {
        return articleDao.deleteAllArticle().subscribeDefault()
    }

    override fun getArticleById(accountId: Int): Single<List<ArticleDbEntity>> {
        return articleDao.getArticleById(accountId).subscribeDefault()
    }
}