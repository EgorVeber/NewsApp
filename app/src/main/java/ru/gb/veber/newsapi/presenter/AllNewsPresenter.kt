package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.database.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepoImpl
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.model.repository.room.SourcesRepoImpl
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.search.searchnews.AllNewsView
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder.Companion.VIEW_TYPE_SEARCH_NEWS

class AllNewsPresenter(
    private val newsRepoImpl: NewsRepoImpl,
    private val router: Router,
    private val articleRepoImpl: ArticleRepoImpl,
    private val roomRepoImpl: RoomRepoImpl,
    private val accountId: Int,
    private val sourcesRepoImpl: SourcesRepoImpl,
    private val accountSourcesRepoImpl: AccountSourcesRepoImpl,
) :
    MvpPresenter<AllNewsView>() {

    private var saveHistory = false
    private var likeSources: List<Sources> = listOf()
    private var allSources: List<Sources> = listOf()
    private var sourcesID: Int = 0


    private var articleListHistory: MutableList<Article> = mutableListOf()
    private val bag = CompositeDisposable()


    fun onBackPressedRouter(): Boolean {
        Log.d("Back", "onBackPressedRouter() SourcesDTO")
        router.exit()
        return true
    }

    fun getAccountSettings(accountId: Int) {
        roomRepoImpl.getAccountById(accountId).subscribe({
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
            likeSources = it
        }, {
            Log.d("getSourcesLike", it.localizedMessage)
        }).disposebleBy(bag)
    }

    private fun getSources() {
        sourcesRepoImpl.getSources().subscribe({
            allSources = it
        }, {
            Log.d("getSourcesLike", it.localizedMessage)
        }).disposebleBy(bag)
    }

    fun saveSources() {
        accountSourcesRepoImpl.insert(AccountSourcesDbEntity(accountId, sourcesID)).subscribe({
            viewState.successSaveSources()
            accountSourcesRepoImpl.getLikeSourcesFromAccount(accountId).subscribe({
                likeSources = it
                Log.d("getSourcesLike", it.toString())
            }, {
                Log.d("getSourcesLike", it.localizedMessage)
            })
        }, {
            Log.d("saveSources", "error saveSources  " + it.localizedMessage)
        }).disposebleBy(bag)
    }


    fun getNews(
        accountId: Int,
        keyWord: String?,
        searchIn: String?,
        sortByKeyWord: String?,
        sortBySources: String?,
        sourcesId: String?,
        dateSources: String?,
        sourcesName: String?,
    ) {

        viewState.setTitle(
            keyWord,
            sourcesName,
            if (!keyWord.isNullOrEmpty()) sortByKeyWord else sortBySources,
            dateSources)


        Single.zip(newsRepoImpl.getEverythingKeyWordSearchInSources(
            sourcesId,
            keyWord,
            searchIn,
            if (!keyWord.isNullOrEmpty()) sortByKeyWord else sortBySources,
            dateSources,
            dateSources
        ).map { articles ->
            articles.articles.map(::mapToArticle).also {
                newsRepoImpl.changeRequest(it)
                it.map { art ->
                    art.viewType = VIEW_TYPE_SEARCH_NEWS
                }
            }
        }, articleRepoImpl.getArticleById(accountId)) { news, articles ->

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
            Log.d("loadNews", "Size it list" + it.size.toString())

            it.forEach { arct ->
                Log.d("loadNews", "list $arct")
            }
        }, {
            Log.d("loadNews", it.localizedMessage)
            viewState.emptyList()
        }).disposebleBy(bag)
    }

    fun clickNews(article: Article) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            var isLikeSources = likeSources.find { it.idSources == article.source.id }?.id ?: 0
            sourcesID = allSources.find { it.idSources == article.source.id }?.id ?: 0
            if (isLikeSources != 0) {
                viewState.hideSaveSources()
            } else if (sourcesID != 0) {
                viewState.showSaveSources()
            }
        }

        viewState.clickNews(article)
        saveArticle(article, accountId)
        if (article.isFavorites) viewState.setLikeResourcesActive()
        else viewState.setLikeResourcesNegative()
        viewState.sheetExpanded()
    }

    private fun saveArticle(article: Article, i: Int) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            if (saveHistory) {
                if (!article.isFavorites && !article.isHistory) {
                    article.isHistory = true
                    articleRepoImpl.insertArticle(mapToArticleDbEntity(article, accountId))
                        .subscribe({
                            articleListHistory.find { it.title == article.title }?.isHistory = true
                            viewState.changeNews(articleListHistory)
                        }, {
                            Log.d(ERROR_DB, it.localizedMessage)
                        }).disposebleBy(bag)
                }
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
        articleRepoImpl.deleteArticleById(article.title.toString(), accountId).subscribe({
            articleListHistory.find { it.title == article.title }?.isFavorites = false
            viewState.changeNews(articleListHistory)
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        }).disposebleBy(bag)
    }

    private fun saveArticleLike(article: Article) {
        var item = mapToArticleDbEntity(article, accountId)
        item.isFavorites = true
        articleRepoImpl.insertArticle(item).subscribe({
            articleListHistory.find { it.title == article.title }?.isFavorites = true
            viewState.changeNews(articleListHistory)
            Log.d(ERROR_DB, "successInsertArticle")
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        }).disposebleBy(bag)
    }


    fun openScreenWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
    }

    fun exit() {
        router.exit()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    override fun onDestroy() {
        super.onDestroy()
        bag.dispose()
    }
}