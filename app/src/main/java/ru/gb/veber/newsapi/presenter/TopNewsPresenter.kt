package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.CountryRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder.Companion.VIEW_TYPE_HEADER_NEWS
import ru.gb.veber.newsapi.view.topnews.pageritem.TopNewsView
import java.util.*

class TopNewsPresenter(
    private val newsRepoImpl: NewsRepoImpl,
    private val router: Router,
    private val articleRepoImpl: ArticleRepoImpl,
    private val roomRepoImpl: RoomRepoImpl,
    private val accountIdPresenter: Int,
    private val countryRepoImpl: CountryRepoImpl,
) :
    MvpPresenter<TopNewsView>() {

    private var saveHistory = false
    private var filterFlag = false
    private val bag = CompositeDisposable()
    private var articleListHistory: MutableList<Article> = mutableListOf()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()

    }

    fun getAccountSettings() {
        if (accountIdPresenter != ACCOUNT_ID_DEFAULT) {
            roomRepoImpl.getAccountById(accountIdPresenter).subscribe({
                saveHistory = it.saveHistory
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            }).disposebleBy(bag)
        } else {
            viewState.hideFavorites()
        }
    }

    fun loadNews(category: String) {
        Single.zip(newsRepoImpl.getTopicalHeadlinesCategoryCountry(category, "ru"),
            articleRepoImpl.getArticleById(accountIdPresenter)) { news, articles ->
            var newsModified = mapToArticleDTO(news).also { newsRepoImpl.changeRequest(it) }
            articles.forEach { art ->
                Log.d("TAG", "loadNews() called with: art = $art")
                newsModified.forEach { new ->
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
            newsModified.also { it[0].viewType = VIEW_TYPE_HEADER_NEWS }
        }.subscribeDefault().subscribe({
            if (it.isEmpty()) {
                viewState.emptyList()
            } else {
                articleListHistory = it.toMutableList()
                viewState.setSources(it)
            }
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        }).disposebleBy(bag)
    }


    fun clickNews(article: Article) {
        if (!filterFlag) {
            if (accountIdPresenter != ACCOUNT_ID_DEFAULT) {
                saveArticle(article)
            }
            viewState.clickNews(article)
            if (article.isFavorites) viewState.setLikeResourcesActive()
            else viewState.setLikeResourcesNegative()
            viewState.hideFilter()
            viewState.sheetExpanded()
        }
    }

    private fun saveArticle(article: Article) {
        if (saveHistory) {
            if (!article.isFavorites && !article.isHistory) {
                article.isHistory = true
                article.dateAdded = Date().formatDateTime()
                articleRepoImpl.insertArticle(mapToArticleDbEntity(article, accountIdPresenter))
                    .subscribe({
                        articleListHistory.find { it.title == article.title }?.isHistory = true
                        viewState.changeNews(articleListHistory)
                    }, {
                        Log.d(ERROR_DB, it.localizedMessage)
                    }).disposebleBy(bag)
            }
        }
    }

    private fun saveArticleLike(article: Article) {
        var item = mapToArticleDbEntity(article, accountIdPresenter)
        item.isFavorites = true
        articleRepoImpl.insertArticle(item).subscribe({
            articleListHistory.find { it.title == article.title }?.isFavorites = true
            viewState.changeNews(articleListHistory)
            Log.d(ERROR_DB, "successInsertArticle")
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        }).disposebleBy(bag)
    }


    private fun deleteFavorites(article: Article) {
        article.isFavorites = false
        articleRepoImpl.deleteArticleById(article.title.toString(), accountIdPresenter).subscribe({
            articleListHistory.find { it.title == article.title }?.isFavorites = false
            viewState.changeNews(articleListHistory)
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        }).disposebleBy(bag)
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

    fun openScreenWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        bag.dispose()
    }


    fun filterButtonClick() {
        if (!filterFlag) {
            viewState.setAlfa()
            viewState.setImageFilterButton()
            viewState.showCountryList()
            viewState.showCancelButton()
        } else {
            //validation
        }
        filterFlag = !filterFlag
    }

    fun cancelButtonClick() {
        filterFlag = !filterFlag
        viewState.setAlfaCancel()
        viewState.setImageFilterButtonCancel()
        viewState.hideCountryList()
        viewState.hideCancelButton()
    }


    fun behaviorCollapsed() {
        viewState.showFilter()
    }

    fun getCountry() {
        countryRepoImpl.getCountry().subscribe({
            viewState.setCountry(it.map { country -> country.id })
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }
}