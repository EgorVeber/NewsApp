package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.common.extentions.DateFormatter.toFormatDateDefault
import ru.gb.veber.newsapi.common.extentions.DateFormatter.toFormatDateNoTime
import ru.gb.veber.newsapi.domain.models.ArticleModel
import ru.gb.veber.newsapi.domain.models.SourceModel
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import ru.gb.veber.newsapi.presentation.mapper.toArticleUiModel
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder
import javax.inject.Inject


//TODO import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder

class FavoritesInteractor @Inject constructor(
    private val articleRepoImpl: ArticleRepo,
) {

    suspend fun getLikeArticle(accountId: Int): List<ArticleModel> {
        return articleRepoImpl.getLikeArticleByIdV2(accountId)
    }

    suspend fun getHistoryArticle(accountId: Int): List<ArticleModel> {
        return articleRepoImpl.getHistoryArticleByIdV2(accountId)
    }

    suspend fun deleteArticleFavorites(title: String, accountId: Int) {
        articleRepoImpl.deleteArticleByIdFavoritesV2(title, accountId)
    }

    suspend fun deleteArticleHistory(title: String, accountId: Int) {
        articleRepoImpl.deleteArticleByIdHistoryV2(title, accountId)
    }

    suspend fun deleteArticleHistoryGroup(accountId: Int, dateAdded: String) {
        articleRepoImpl.deleteArticleByIdHistoryGroupV2(accountId, dateAdded)
    }

    fun getListArticleGroupByDate(listArticle: List<ArticleModel>): List<ArticleUiModel> {
        val mutableList: MutableList<ArticleModel> = mutableListOf()
        listArticle.reversed().map { article ->
            article.publishedAt = article.publishedAt.toFormatDateDefault()
            article.dateAdded = article.dateAdded.toFormatDateNoTime()
            article
        }.sortedBy { reversedArticle ->
            reversedArticle.dateAdded
        }.reversed().groupBy { groupArticle ->
            groupArticle.dateAdded
        }.forEach { group ->
            mutableList.add(getNewArticleHistory((group.key), group.value.size))
            group.value.reversed().forEach { groupReversedArticle ->
                mutableList.add(groupReversedArticle)
            }
        }
        return mutableList.map { article -> article.toArticleUiModel(true) }
    }

    private fun getNewArticleHistory(groupTitleDate: String, countArticleDate: Int): ArticleModel {
        return ArticleModel(
            id = -1,
            author = countArticleDate.toString(),
            description = "",
            publishedAt = groupTitleDate,
            sourceModel = SourceModel("0", ""),
            title = SHOW_HISTORY,
            url = "",
            urlToImage = "",
            dateAdded = "",
        )
    }

    companion object {
        private const val SHOW_HISTORY = "SHOW_HISTORY"
    }

}