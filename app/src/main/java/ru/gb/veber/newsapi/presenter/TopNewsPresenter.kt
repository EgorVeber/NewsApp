package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.topnews.pageritem.TopNewsView

class TopNewsPresenter(
    private val newsRepoImpl: NewsRepoImpl,
    private val router: Router,
    private val articleRepoImpl: ArticleRepoImpl,
    private val roomRepoImpl: RoomRepoImpl,
) :
    MvpPresenter<TopNewsView>() {

    private var checkFilter = false
    private var currentArticle = 0
    private var saveHistory = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun getAccountSettings(accountId: Int) {
        roomRepoImpl.getAccountById(accountId).subscribe({
            saveHistory = it.saveHistory
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun clickNews(it: Article) {
        viewState.clickNews(it)
    }

    fun loadNews(category: String, accountID: Int) {
        Single.zip(newsRepoImpl.getTopicalHeadlinesCategoryCountry(category, "ru").map { articles ->
            articles.articles.map(::mapToArticle).also {
                newsRepoImpl.changeRequest(it)
            }
        }, articleRepoImpl.getArticleById(accountID)) { news, articles ->
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
            Log.d("loadNews", it.toString())
            Log.d("loadNews", it.size.toString())
            viewState.setSources(it)
        }, {
            Log.d("loadNews", it.localizedMessage)
        })
    }

    fun saveArticle(article: Article, accountId: Int) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            if (saveHistory) {
                if (!article.isFavorites&& !article.isHistory) {
                    article.isHistory = true
                    articleRepoImpl.insertArticle(mapToArticleDbEntity(article, accountId)).andThen(
                        articleRepoImpl.getLastArticle()
                    ).subscribe({
                        currentArticle = it.id
                        viewState.successInsertArticle()
                    }, {
                        Log.d(ERROR_DB, it.localizedMessage)
                    })
                }
            }
        }
    }

    fun saveArticleLike(article: Article, accountId: Int) {
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

    fun deleteFavorites(article: Article) {
        articleRepoImpl.deleteArticleById(article.title).subscribe({
            Log.d("SUCCESS_DELETE", "SUCCESS DELETE BY ID")
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }

    fun loadNewsCountry(category: String, country: String) {
        newsRepoImpl.getTopicalHeadlinesCategoryCountry(category, country).map { articles ->
            articles.articles.map(::mapToArticle).also {
                newsRepoImpl.changeRequest(it)
            }
        }.subscribe({
            viewState.setSources(it)
        }, {
            Log.d("TAG", it.localizedMessage)
        })
    }


    fun filterButtonClick() {
        checkFilter = if (!checkFilter) {
            viewState.showFilter()
            true
        } else {
            viewState.hideFilter()
            false
        }
    }

    fun behaviorHide() {
        checkFilter = false
    }

    fun openScreenWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
    }
}