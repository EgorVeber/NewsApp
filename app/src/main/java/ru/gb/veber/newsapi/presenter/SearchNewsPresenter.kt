package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.HistorySelect
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.database.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.model.network.ChangeRequestHelper
import ru.gb.veber.newsapi.model.repository.network.NewsRepo
import ru.gb.veber.newsapi.model.repository.room.AccountRepo
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepo
import ru.gb.veber.newsapi.model.repository.room.ArticleRepo
import ru.gb.veber.newsapi.model.repository.room.SourcesRepo
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.search.searchnews.SearchNewsView
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder.Companion.VIEW_TYPE_SEARCH_NEWS
import java.util.*
import javax.inject.Inject

class SearchNewsPresenter(
    private val accountId: Int,
) :
    MvpPresenter<SearchNewsView>() {

    @Inject
    lateinit var articleRepoImpl: ArticleRepo

    @Inject
    lateinit var accountRepoImpl: AccountRepo

    @Inject
    lateinit var sourcesRepoImpl: SourcesRepo

    @Inject
    lateinit var accountSourcesRepoImpl: AccountSourcesRepo

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var newsRepoImpl: NewsRepo

    @Inject
    lateinit var changeRequestHelper: ChangeRequestHelper

    private var saveHistory = false

    private var articleListHistory: MutableList<Article> = mutableListOf()
    private var likeSources: MutableList<Sources> = mutableListOf()
    private var allSources: List<Sources> = listOf()

    private var sourcesID: Int = 0
    private val bag = CompositeDisposable()


    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun getAccountSettings() {
        accountRepoImpl.getAccountById(accountId).subscribe({
            saveHistory = it.saveHistory
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
        if (accountId == ACCOUNT_ID_DEFAULT) {
            viewState.hideFavorites()
        } else {
            getSourcesLike()
            getSources()
        }
    }

    private fun getSourcesLike() {
        accountSourcesRepoImpl.getLikeSourcesFromAccount(accountId).subscribe({
            likeSources = it.toMutableList()
        }, {
        }).disposableBy(bag)
    }

    private fun getSources() {
        sourcesRepoImpl.getSources().subscribe({
            allSources = it
        }, {
        }).disposableBy(bag)
    }


    fun getNews(historySelect: HistorySelect?) {

        viewState.setTitle(
            keyWord = historySelect?.keyWord,
            sourcesId = historySelect?.sourcesName,
            s = if (!historySelect?.keyWord.isNullOrEmpty()) historySelect?.sortByKeyWord else historySelect?.sortBySources,
            dateSources = historySelect?.dateSources
        )


        Single.zip(newsRepoImpl.getEverythingKeyWordSearchInSources(
            sources = historySelect?.sourcesId,
            q = historySelect?.keyWord,
            searchIn = historySelect?.searchIn,
            sortBy = if (!historySelect?.keyWord.isNullOrEmpty()) historySelect?.sortByKeyWord else historySelect?.sortBySources,
            from = historySelect?.dateSources,
            to = historySelect?.dateSources,
            key = API_KEY_NEWS
        ).map { articles ->
            articles.articles.map(::mapToArticle).also {
                changeRequestHelper.changeRequest(it)
                it.map { art ->
                    art.viewType = VIEW_TYPE_SEARCH_NEWS
                }
            }
        }, articleRepoImpl.getArticleById(accountId)
        ) { news, articles ->

            articles.forEach { art ->
                news.forEach { new ->
                    if (art.title == new.title) {
                        if (art.isFavorites) {
                            new.isFavorites = true
                        }
                        if (art.isHistory) {
                            new.isHistory = true
                        }
                    }
                }
            }
            news
        }.subscribe({
            if (it.isEmpty()) {
                viewState.emptyList()
            } else {
                articleListHistory = it.toMutableList()
                viewState.setNews(it)
            }
        }, {
            viewState.emptyList()
        }).disposableBy(bag)
    }

    fun clickNews(article: Article) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            var isLikeSources = likeSources.find { it.idSources == article.source.id }?.id ?: 0
            sourcesID = allSources.find { it.idSources == article.source.id }?.id ?: 0

            if (isLikeSources != 0 || sourcesID == 0) {
                viewState.hideSaveSources()
            } else {
                viewState.showSaveSources()
            }
            saveArticle(article)
        }
        viewState.clickNews(article)
        if (article.isFavorites) viewState.setLikeResourcesActive()
        else viewState.setLikeResourcesNegative()
        viewState.sheetExpanded()
    }


    fun saveSources() {
        accountSourcesRepoImpl.insert(AccountSourcesDbEntity(accountId, sourcesID)).subscribe({
            viewState.successSaveSources()
            getSourcesLike()
        }, {
        }).disposableBy(bag)
    }


    private fun saveArticle(article: Article) {
        if (saveHistory) {
            if (!article.isFavorites && !article.isHistory) {
                article.isHistory = true
                article.dateAdded = Date().formatDateTime()
                articleRepoImpl.insertArticle(mapToArticleDbEntity(article, accountId))
                    .subscribe({
                        articleListHistory.find { it.title == article.title }?.isHistory = true
                        viewState.changeNews(articleListHistory)
                    }, {
                        Log.d(ERROR_DB, it.localizedMessage)
                    }).disposableBy(bag)
            }
        }
    }


    fun setOnClickImageFavorites(article: Article) {
        if (article.isFavorites) {
            viewState.setLikeResourcesNegative()
            viewState.removeBadge()

            deleteFavorites(article)
            article.isFavorites = false

        } else {
            viewState.addBadge()
            viewState.setLikeResourcesActive()

            saveArticleLike(article)
            article.isFavorites = true
        }
    }

    private fun deleteFavorites(article: Article) {
        article.isFavorites = false
        articleRepoImpl.deleteArticleByIdFavorites(article.title.toString(), accountId).subscribe({
            articleListHistory.find { it.title == article.title }?.isFavorites = false
            viewState.changeNews(articleListHistory)
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        }).disposableBy(bag)
    }

    private fun saveArticleLike(article: Article) {
        var item = mapToArticleDbEntity(article, accountId)
        item.isFavorites = true
        articleRepoImpl.insertArticle(item).subscribe({
            articleListHistory.find { it.title == article.title }?.isFavorites = true
            viewState.changeNews(articleListHistory)
        }, {
        }).disposableBy(bag)
    }


    fun openScreenWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
    }

    fun exit() {
        router.exit()
    }

    override fun onDestroy() {
        super.onDestroy()
        bag.dispose()
    }
}