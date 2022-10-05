package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.favorites.FavoritesView
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerAdapter.Companion.FAVORITES
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder.Companion.VIEW_TYPE_FAVORITES_NEWS

class FavoritesPresenter(
    private val router: Router,
    private val articleRepoImpl: ArticleRepoImpl,
) :
    MvpPresenter<FavoritesView>() {

    private var accountIdS: Int = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun onBackPressedRouter(): Boolean {
        Log.d("Back", "onBackPressedRouter() SourcesDTO")
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
                        viewState.setSources(it.map(::articleDbEntityToArticle).reversed().map {
                            it.publishedAtChange = stringFromData(it.publishedAt).formatDateTime()
                            it
                        })
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
        var articleNew = article.copy()
        articleRepoImpl.deleteArticleById(articleNew.title)
            .andThen(articleRepoImpl.getLikeArticleById(accountIdS)).subscribe({
            viewState.updateFavorites(it.map(::articleDbEntityToArticle).map {
                it.publishedAtChange = stringFromData(it.publishedAt).formatDateTime()
                it.viewType = VIEW_TYPE_FAVORITES_NEWS
                it
            })
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }
}