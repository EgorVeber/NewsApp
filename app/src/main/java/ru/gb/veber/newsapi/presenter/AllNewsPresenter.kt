package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.Article
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

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

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
        Log.d("loadNews",
            "getNews() called with: accountId = $accountId, keyWord = $keyWord, searchIn = $searchIn, sortByKeyWord = $sortByKeyWord, sortBySources = $sortBySources, sourcesId = $sourcesId, dateSources = $dateSources")
        if (accountId == ACCOUNT_ID_DEFAULT) {
            viewState.hideFavorites()
        }
        viewState.setTitle(keyWord,
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

//            news.forEach {
//                it.publishedAt = stringFromData(it.publishedAt).formatDate()
//                Log.d("forEach", "forEach  = $it")
//            }
//            Log.d("forEach", "groupBy")
//            var i = 0;
//            news.groupBy { it.publishedAt }.forEach {
//                Log.d("forEach", "Key $i = " + it.key!!)
//                Log.d("forEach", "Value $i (${it.value.last().viewType})= " + it.value.toString())
//                i++
//            }


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
                viewState.setNews(it)
            }
        }, {
            Log.d("loadNews", it.localizedMessage)
            viewState.emptyList()
        })
    }

    fun clickNews(article: Article) {
        viewState.clickNews(article)
    }

    fun saveArticle(article: Article, i: Int) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            if (saveHistory) {
                if (!article.isFavorites && !article.isHistory) {
                    var articleNew = article.copy(isHistory = true)
                    articleRepoImpl.insertArticle(mapToArticleDbEntity(articleNew, accountId))
                        .subscribe({
                            viewState.successInsertArticle()
                        }, {
                            Log.d(ERROR_DB, it.localizedMessage)
                        })
                }
            }
        }
    }

    fun openScreenWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
    }

    fun deleteFavorites(article: Article) {
        article.title?.let {
            articleRepoImpl.deleteArticleById(it, accountId).subscribe({
                Log.d("SUCCESS_DELETE", "SUCCESS DELETE BY ID")
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        }
    }

    fun saveArticleLike(article: Article) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            if (!article.isFavorites) {
                var item = mapToArticleDbEntity(article, accountId)
                item.isFavorites = true
                articleRepoImpl.insertArticle(item).subscribe({
                    viewState.successInsertArticle()
                    Log.d(ERROR_DB, "successInsertArticle")
                }, {
                    Log.d(ERROR_DB, it.localizedMessage)
                })
            }
        }
    }

    fun exit() {
        router.exit()
    }
}