package ru.gb.veber.newsapi.data.repository
import ru.gb.veber.newsapi.data.mapper.toArticle
import ru.gb.veber.newsapi.data.mapper.toArticleDbEntity
import ru.gb.veber.newsapi.data.models.room.dao.ArticleDao
import ru.gb.veber.newsapi.domain.models.Article
import ru.gb.veber.newsapi.domain.repository.ArticleRepo

class ArticleRepoImpl(private val articleDao: ArticleDao) : ArticleRepo {

    override suspend fun insertArticleV2(article: Article, accountId: Int) {
        articleDao.insertArticleV2(article.toArticleDbEntity(accountId))
    }

    override suspend fun updateArticleV2(article: Article, accountId: Int) {
        articleDao.updateArticleV2(article.toArticleDbEntity(accountId))
    }

    override suspend fun deleteArticleV2(article: Article, accountId: Int) {
        articleDao.deleteArticleV2(article.toArticleDbEntity(accountId))
    }

    override suspend fun deleteAllArticleV2() {
        articleDao.deleteAllArticleV2()
    }

    override suspend fun getHistoryArticleByIdV2(accountId: Int): List<Article> {
        return articleDao.getHistoryArticleByIdV2(accountId)
            .map { articleDbEntity -> articleDbEntity.toArticle() }
    }

    override suspend fun getLikeArticleByIdV2(accountId: Int): List<Article> {
        return articleDao.getLikeArticleByIdV2(accountId)
            .map { articleDbEntity -> articleDbEntity.toArticle() }
    }

    override suspend fun getLastArticleV2(): Article {
        return articleDao.getLastArticleV2().toArticle()
    }

    override suspend fun getArticleByIdV2(accountId: Int): List<Article> {
        return articleDao.getArticleByIdV2(accountId)
            .map { articleDbEntity -> articleDbEntity.toArticle() }
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
