package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.Country
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.network.NewsRepo
import ru.gb.veber.newsapi.model.repository.room.AccountRepo
import ru.gb.veber.newsapi.model.repository.room.ArticleRepo
import ru.gb.veber.newsapi.model.repository.room.CountryRepo
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.utils.mapper.*
import ru.gb.veber.newsapi.view.topnews.pageritem.BaseViewHolder.Companion.VIEW_TYPE_TOP_NEWS_HEADER
import ru.gb.veber.newsapi.view.topnews.pageritem.TopNewsView
import java.util.*
import javax.inject.Inject

class TopNewsPresenter(
    private val accountIdPresenter: Int,
) :
    MvpPresenter<TopNewsView>() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var newsRepoImpl: NewsRepo

    @Inject
    lateinit var sharedPreferenceAccount: SharedPreferenceAccount


    @Inject
    lateinit var articleRepoImpl: ArticleRepo

    @Inject
    lateinit var accountRepoImpl: AccountRepo

    @Inject
    lateinit var countryRepoImpl: CountryRepo


    private lateinit var account: Account
    private var saveHistory = false
    private var filterFlag = false

    private var articleListHistory: MutableList<Article> = mutableListOf()
    private var listCountry: MutableList<Country> = mutableListOf()

    private val bag = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun getAccountSettings() {
        if (accountIdPresenter != ACCOUNT_ID_DEFAULT) {
            accountRepoImpl.getAccountById(accountIdPresenter).subscribe({
                account = it
                saveHistory = it.saveHistory
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            }).disposableBy(bag)
        } else {
            viewState.hideFavorites()
        }
    }

    fun loadNews(category: String) {
        var countryCode = sharedPreferenceAccount.getAccountCountryCode()
        Single.zip(newsRepoImpl.getTopicalHeadlinesCategoryCountry(category = category, country = countryCode, key = API_KEY_NEWS),
            articleRepoImpl.getArticleById(accountIdPresenter)) { news, articles ->
            var newsModified = news.articles.map { articleDto->
                articleDto.toArticle()
            }.also { list ->
                list.map {article->
                    article.toArticleUI()
                }
            }
            articles.forEach { art ->
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
            newsModified.also { it[0].viewType = VIEW_TYPE_TOP_NEWS_HEADER }
        }.subscribeDefault().subscribe({
            if (it.isEmpty()) {
                viewState.emptyList()
            } else {
                articleListHistory = it.toMutableList()
                viewState.setSources(it)
            }
        }, {
            viewState.emptyList()
            Log.d(ERROR_DB, it.localizedMessage)
        }).disposableBy(bag)
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
                    }).disposableBy(bag)
            }
        }
    }

    private fun saveArticleLike(article: Article) {
        var item = mapToArticleDbEntity(article, accountIdPresenter)
        item.isFavorites = true
        articleRepoImpl.insertArticle(item).subscribe({
            articleListHistory.find { it.title == article.title }?.isFavorites = true
            viewState.changeNews(articleListHistory)
        }, {
        }).disposableBy(bag)
    }

    private fun deleteFavorites(article: Article) {
        article.isFavorites = false
        articleRepoImpl.deleteArticleByIdFavorites(article.title.toString(), accountIdPresenter)
            .subscribe({
                articleListHistory.find { it.title == article.title }?.isFavorites = false
                viewState.changeNews(articleListHistory)
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            }).disposableBy(bag)
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

    fun filterButtonClick(country: String) {
        if (!filterFlag) {
            viewState.fadeRecyclerShowCountry()
            filterFlag = !filterFlag
        } else {
            if (country.isEmpty() || !listCountry.map { it.id }.contains(country)) {
                viewState.errorCountry()
            } else {
                var countryCode = listCountry.find { it.id == country }?.code ?: ALL_COUNTRY_VALUE
                sharedPreferenceAccount.setAccountCountryCode(countryCode)
                sharedPreferenceAccount.setAccountCountry(country)
                viewState.updateViewPagerEvent()
                if (accountIdPresenter != ACCOUNT_ID_DEFAULT) {
                    account.myCountry = country
                    account.countryCode = countryCode
                    accountRepoImpl.updateAccount(account.toAccountDbEntity()).subscribe({
                    }, {
                        Log.d(ERROR_DB, it.localizedMessage)
                    })
                }
            }
        }
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
        countryRepoImpl.getCountry().subscribe({ country ->
            listCountry = country.map { countryDbEntity ->
                countryDbEntity.toCountry()
            } as MutableList<Country>
            listCountry.add(0, Country(ALL_COUNTRY, ALL_COUNTRY_VALUE))
            var list: MutableList<String> =
                listCountry.map { country -> country.id }.sortedBy { it } as MutableList<String>
            var index = list.indexOf(sharedPreferenceAccount.getAccountCountry())
            if (index == -1) {
                index = 0
            }
            viewState.setCountry(list, index)
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }
}