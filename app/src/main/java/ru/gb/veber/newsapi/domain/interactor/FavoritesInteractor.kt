package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.domain.models.ArticleModel
import ru.gb.veber.newsapi.domain.models.SourceModel
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import javax.inject.Inject

class FavoritesInteractor @Inject constructor(
    private val articleRepoImpl: ArticleRepo,
) {

    suspend fun getLikeArticle(accountId: Int): List<ArticleModel> {
        return articleRepoImpl.getLikeArticleByIdV2(accountId).reversed()
    }

    suspend fun getHistoryArticle(accountId: Int): List<ArticleModel> =
        articleRepoImpl.getHistoryArticleById(accountId)

    suspend fun deleteArticleFavorites(title: String, accountId: Int) {
        articleRepoImpl.deleteArticleByIdFavorites(title, accountId)
    }

    suspend fun deleteArticleHistory(title: String, accountId: Int) {
        articleRepoImpl.deleteArticleByIdHistory(title, accountId)
    }

    suspend fun deleteArticleHistoryGroup(accountId: Int, dateAdded: String) {
        articleRepoImpl.deleteArticleByIdHistoryGroup(accountId, dateAdded)
    }

    suspend fun getListArticlesGroupByDate(
        accountID: Int,
        keyTitle: String,
    ): List<ArticleModel> {
        val historyArticle: List<ArticleModel> = getHistoryArticle(accountID)
        val mutableList: MutableList<ArticleModel> = mutableListOf()
        if (historyArticle.isEmpty()) return emptyList()

        historyArticle.reversed().sortedBy { reversedArticle ->
            reversedArticle.dateAdded
        }.reversed().groupBy { groupArticle ->
            groupArticle.dateAdded
        }.forEach { group ->
            mutableList.add(getNewHeaderArticleHistory((group.key), group.value.size, keyTitle))
            group.value.reversed().forEach { groupReversedArticle ->
                mutableList.add(groupReversedArticle)
            }
        }
        return mutableList
    }

    private fun getNewHeaderArticleHistory(
        groupTitleDate: String,
        countArticleDate: Int,
        keyTitle: String,
    ): ArticleModel {
        return ArticleModel(
            id = -1,
            author = countArticleDate.toString(),
            description = "",
            publishedAt = "",
            sourceModel = SourceModel.empty,
            title = keyTitle,
            url = "",
            urlToImage = "",
            dateAdded = groupTitleDate,
        )
    }
}