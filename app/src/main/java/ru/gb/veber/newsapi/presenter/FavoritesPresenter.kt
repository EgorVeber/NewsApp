package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.network.ChangeRequestHelper
import ru.gb.veber.newsapi.model.repository.room.ArticleRepo
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.favorites.FavoritesView
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerAdapter.Companion.FAVORITES
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder.Companion.VIEW_TYPE_FAVORITES_NEWS
import javax.inject.Inject

class FavoritesPresenter(
    // private val articleRepoImpl: ArticleRepoImpl,
) :
    MvpPresenter<FavoritesView>() {

    private var accountIdS: Int = 0
    private var listSave: MutableList<Article> = mutableListOf()

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var changeRequestHelper: ChangeRequestHelper
    @Inject
    lateinit var articleRepoImpl: ArticleRepo

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun getAccountArticle(accountID: Int, page: String) {
        accountIdS = accountID
        if (accountID != ACCOUNT_ID_DEFAULT) {
            if (page == FAVORITES) {
                viewState.loading()
                articleRepoImpl.getLikeArticleById(accountID).subscribe({
                    if (it.isEmpty()) {
                        viewState.emptyList()
                    } else {
                        viewState.setSources(it.map(::articleDbEntityToArticle).reversed().map {
                            it.publishedAtChange = stringFromData(it.publishedAt).formatDateTime()
                            it.viewType = VIEW_TYPE_FAVORITES_NEWS
                            it
                        })
                    }
                }, {
                    Log.d(ERROR_DB, it.localizedMessage)
                })
            } else {
                viewState.loading()
                articleRepoImpl.getHistoryArticleById(accountID).subscribe({
                    if (it.isEmpty()) {
                        viewState.emptyList()
                    } else {
                        listSave =
                            changeRequestHelper.changeHistoryList(it.map(::articleDbEntityToArticle))
                        viewState.setSources(listSave)
                    }
                }, {
                    Log.d(ERROR_DB, it.localizedMessage)
                })
            }
        } else {
            viewState.notAuthorized()
        }
    }

    fun clickNews(it: Article) {
        viewState.clickNews(it)
    }

    fun deleteFavorites(article: Article) {
        article.title?.let { title ->
            articleRepoImpl.deleteArticleByIdFavorites(title, accountIdS)
                .andThen(articleRepoImpl.getLikeArticleById(accountIdS)).subscribe({ list ->
                    viewState.setSources(list.map(::articleDbEntityToArticle).reversed()
                        .map { art ->
                            art.publishedAtChange = stringFromData(art.publishedAt).formatDateTime()
                            art.viewType = VIEW_TYPE_FAVORITES_NEWS
                            art
                        })
                }, {
                    Log.d(ERROR_DB, it.localizedMessage)
                })
        }
    }

    fun openScreenWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
    }

    fun deleteHistory(article: Article) {
        article.title?.let { title ->
            articleRepoImpl.deleteArticleByIdHistory(title, accountIdS)
                .andThen(articleRepoImpl.getHistoryArticleById(accountIdS)).subscribe({
                    listSave =
                        changeRequestHelper.changeHistoryList(it.map(::articleDbEntityToArticle))
                    viewState.setSources(listSave)
                }, {
                    Log.d(ERROR_DB, it.localizedMessage)
                })
        }
    }

    fun clickGroupHistory(article: Article) {
        if (article.title == SHOW_HISTORY) {
            listSave.forEach {
                if (it.dateAdded == article.publishedAt) {
                    it.showHistory = false
                }
            }
            article.title = HIDE_HISTORY
            viewState.setSources(listSave.filter { it.showHistory })
        } else {
            listSave.forEach {
                if (it.dateAdded == article.publishedAt) {
                    it.showHistory = true
                }
            }
            article.title = SHOW_HISTORY
            viewState.setSources(listSave.filter { it.showHistory })
        }
    }

    fun deleteGroupHistory(article: Article) {
        articleRepoImpl.deleteArticleByIdHistoryGroup(accountIdS, article.publishedAt).subscribe({
            Log.d(ERROR_DB, "success")
            articleRepoImpl.getHistoryArticleById(accountIdS).subscribe({
                listSave =
                    changeRequestHelper.changeHistoryList(it.map(::articleDbEntityToArticle))
                viewState.setSources(listSave)
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }
}