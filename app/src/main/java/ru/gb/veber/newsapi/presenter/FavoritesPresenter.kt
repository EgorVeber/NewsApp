package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.repository.room.ArticleRepo
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.utils.mapper.HIDE_HISTORY
import ru.gb.veber.newsapi.utils.mapper.SHOW_HISTORY
import ru.gb.veber.newsapi.utils.mapper.toArticle
import ru.gb.veber.newsapi.utils.mapper.toNewListArticleGroupByDate
import ru.gb.veber.newsapi.view.favorites.FavoritesView
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerAdapter.Companion.FAVORITES
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.BaseViewHolder.Companion.VIEW_TYPE_FAVORITES_NEWS
import javax.inject.Inject

class FavoritesPresenter :
    MvpPresenter<FavoritesView>() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var articleRepoImpl: ArticleRepo

    private var accountIdS: Int = 0
    private var listSave: MutableList<Article> = mutableListOf()

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
                articleRepoImpl.getLikeArticleById(accountID).subscribe({ listArticleDb ->
                    if (listArticleDb.isEmpty()) {
                        viewState.emptyList()
                    } else {
                        viewState.setSources(listArticleDb.map { articleDb ->
                            articleDb.toArticle()
                        }.reversed().map { article ->
                            article.publishedAtChange = stringFromData(article.publishedAt).formatDateTime()
                            article.viewType = VIEW_TYPE_FAVORITES_NEWS
                            article
                        })
                    }
                }, {
                    Log.d(ERROR_DB, it.localizedMessage)
                })
            } else {
                viewState.loading()
                articleRepoImpl.getHistoryArticleById(accountID).subscribe({ listArticle ->
                    if (listArticle.isEmpty()) {
                        viewState.emptyList()
                    } else {
                        listSave = listArticle.map { articleDb ->
                            articleDb.toArticle()
                        }.toNewListArticleGroupByDate()
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
                    viewState.setSources(list.map { articleDb ->
                        articleDb.toArticle()
                    }.reversed()
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
                .andThen(articleRepoImpl.getHistoryArticleById(accountIdS)).subscribe({ list ->
                    listSave = list.map { articleDb ->
                        articleDb.toArticle()
                    }.toNewListArticleGroupByDate()
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
            articleRepoImpl.getHistoryArticleById(accountIdS).subscribe({ list ->
                listSave = list.map { articleDb ->
                    articleDb.toArticle()
                }.toNewListArticleGroupByDate()
                viewState.setSources(listSave)
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }
}