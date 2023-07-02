package ru.gb.veber.newsapi.data.repository
import ru.gb.veber.newsapi.data.database.dao.ArticleDao
import ru.gb.veber.newsapi.data.mapper.toArticleDbEntity
import ru.gb.veber.newsapi.data.mapper.toArticleModel
import ru.gb.veber.newsapi.domain.models.ArticleModel
import ru.gb.veber.newsapi.domain.repository.ArticleRepo

class ArticleRepoImpl(private val articleDao: ArticleDao) : ArticleRepo {

    override suspend fun insertArticleV2(articleModel: ArticleModel, accountId: Int) {
        articleDao.insertArticleV2(articleModel.toArticleDbEntity(accountId))
    }

    override suspend fun updateArticleV2(articleModel: ArticleModel, accountId: Int) {
        articleDao.updateArticleV2(articleModel.toArticleDbEntity(accountId))
    }

    override suspend fun deleteArticleV2(articleModel: ArticleModel, accountId: Int) {
        articleDao.deleteArticleV2(articleModel.toArticleDbEntity(accountId))
    }

    override suspend fun deleteAllArticleV2() {
        articleDao.deleteAllArticleV2()
    }

    override suspend fun getHistoryArticleByIdV2(accountId: Int): List<ArticleModel> {
        return articleDao.getHistoryArticleByIdV2(accountId)
            .map { articleDbEntity -> articleDbEntity.toArticleModel() }
    }

    override suspend fun getLikeArticleByIdV2(accountId: Int): List<ArticleModel> {
        return articleDao.getLikeArticleByIdV2(accountId)
            .map { articleDbEntity -> articleDbEntity.toArticleModel() }
    }

    override suspend fun getLastArticleV2(): ArticleModel {
        return articleDao.getLastArticleV2().toArticleModel()
    }

    override suspend fun getArticleByIdV2(accountId: Int): List<ArticleModel> {
        return articleDao.getArticleByIdV2(accountId)
            .map { articleDbEntity -> articleDbEntity.toArticleModel() }
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

    override suspend fun deleteArticleByIdHistoryV2(title: String, accountId: Int) {
        articleDao.deleteArticleByIdHistoryV2(title, accountId)
    }

    override suspend fun deleteArticleByIdHistoryGroupV2(accountId: Int, dateAdded: String) {
        articleDao.deleteArticleByIdHistoryGroupV2(accountId, dateAdded)
    }
}
