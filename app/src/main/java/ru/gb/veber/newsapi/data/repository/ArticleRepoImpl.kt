package ru.gb.veber.newsapi.data.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import ru.gb.veber.newsapi.data.models.room.dao.ArticleDao
import ru.gb.veber.newsapi.data.models.room.entity.ArticleDbEntity
import ru.gb.veber.newsapi.core.utils.extentions.subscribeDefault

class ArticleRepoImpl(private val articleDao: ArticleDao) : ArticleRepo {

    override fun insertArticle(articleDbEntity: ArticleDbEntity): Completable {
        return articleDao.insertArticle(articleDbEntity).subscribeDefault()
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

    override fun getHistoryArticleById(accountId: Int): Single<List<ArticleDbEntity>> {
        return articleDao.getHistoryArticleById(accountId).subscribeDefault()
    }

    override fun getLikeArticleById(accountId: Int): Single<List<ArticleDbEntity>> {
        return articleDao.getLikeArticleById(accountId).subscribeDefault()
    }

    override fun getLastArticle(): Single<ArticleDbEntity> {
        return articleDao.getLastArticle().subscribeDefault()
    }

    override fun getArticleById(accountId: Int): Single<List<ArticleDbEntity>> {
        return articleDao.getArticleById(accountId).subscribeDefault()
    }

    override fun deleteArticleIsFavoriteById(accountId: Int): Completable {
        return articleDao.deleteArticleIsFavoriteById(accountId).subscribeDefault()
    }

    override fun deleteArticleIsHistoryById(accountId: Int): Completable {
        return articleDao.deleteArticleIsHistoryById(accountId).subscribeDefault()
    }

    override fun deleteArticleByIdFavorites(titile: String, accountId: Int): Completable {
        return articleDao.deleteArticleByIdFavorites(titile, accountId).subscribeDefault()
    }

    override fun deleteArticleByIdHistory(title: String, accountId: Int): Completable {
        return articleDao.deleteArticleByIdHistory(title, accountId).subscribeDefault()
    }

    override fun deleteArticleByIdHistoryGroup(accountId: Int, dateAdded: String): Completable {
        return articleDao.deleteArticleByIdHistoryGroup(accountId,dateAdded).subscribeDefault()
    }

    override suspend fun insertArticleV2(articleDbEntity: ArticleDbEntity) {
         articleDao.insertArticleV2(articleDbEntity)
    }

    override suspend fun updateArticleV2(articleDbEntity: ArticleDbEntity){
         articleDao.updateArticleV2(articleDbEntity)
    }

    override suspend fun deleteArticleV2(articleDbEntity: ArticleDbEntity) {
         articleDao.deleteArticleV2(articleDbEntity)
    }

    override suspend fun deleteAllArticleV2() {
         articleDao.deleteAllArticleV2()
    }

    override suspend fun getHistoryArticleByIdV2(accountId: Int): List<ArticleDbEntity> {
        return articleDao.getHistoryArticleByIdV2(accountId)
    }

    override suspend fun getLikeArticleByIdV2(accountId: Int): List<ArticleDbEntity> {
        return articleDao.getLikeArticleByIdV2(accountId)
    }

    override suspend fun getLastArticleV2(): ArticleDbEntity {
        return articleDao.getLastArticleV2()
    }

    override suspend fun getArticleByIdV2(accountId: Int): List<ArticleDbEntity> {
        return articleDao.getArticleByIdV2(accountId)
    }

    override suspend fun deleteArticleIsFavoriteByIdV2(accountId: Int) {
         articleDao.deleteArticleIsFavoriteByIdV2(accountId)
    }

    override suspend fun deleteArticleIsHistoryByIdV2(accountId: Int) {
         articleDao.deleteArticleIsHistoryByIdV2(accountId)
    }

    override suspend fun deleteArticleByIdFavoritesV2(titile: String, accountId: Int) {
         articleDao.deleteArticleByIdFavoritesV2(titile, accountId)
    }

    override suspend fun deleteArticleByIdHistoryV2(title: String, accountId: Int){
         articleDao.deleteArticleByIdHistoryV2(title, accountId)
    }

    override suspend fun deleteArticleByIdHistoryGroupV2(accountId: Int, dateAdded: String) {
         articleDao.deleteArticleByIdHistoryGroupV2(accountId,dateAdded)
    }
}
