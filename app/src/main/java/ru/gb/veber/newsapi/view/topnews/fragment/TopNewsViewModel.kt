package ru.gb.veber.newsapi.view.topnews.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.Country
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.network.NewsRepo
import ru.gb.veber.newsapi.model.repository.room.AccountRepo
import ru.gb.veber.newsapi.model.repository.room.ArticleRepo
import ru.gb.veber.newsapi.model.repository.room.CountryRepo
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.ALL_COUNTRY
import ru.gb.veber.newsapi.utils.ALL_COUNTRY_VALUE
import ru.gb.veber.newsapi.utils.API_KEY_NEWS
import ru.gb.veber.newsapi.utils.ERROR_DB
import ru.gb.veber.newsapi.utils.ERROR_LOAD_NEWS
import ru.gb.veber.newsapi.utils.disposableBy
import ru.gb.veber.newsapi.utils.formatDateTime
import ru.gb.veber.newsapi.utils.mapper.toAccountDbEntity
import ru.gb.veber.newsapi.utils.mapper.toArticle
import ru.gb.veber.newsapi.utils.mapper.toArticleDbEntity
import ru.gb.veber.newsapi.utils.mapper.toArticleUI
import ru.gb.veber.newsapi.utils.mapper.toCountry
import ru.gb.veber.newsapi.utils.subscribeDefault
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.BaseViewHolder
import java.util.Date
import javax.inject.Inject

class TopNewsViewModel @Inject constructor(
    private val router: Router,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val newsRepoImpl: NewsRepo,
    private val articleRepoImpl: ArticleRepo,
    private val accountRepoImpl: AccountRepo,
    private val countryRepoImpl: CountryRepo,
) : ViewModel() {

    private val mutableFlow: MutableLiveData<TopNewsState> = MutableLiveData()
    private val flow: LiveData<TopNewsState> = mutableFlow

    private lateinit var account: Account
    private var accountId: Int = 0

    private var saveHistory = false
    private var filterFlag = false

    private var articleListHistory: MutableList<Article> = mutableListOf()
    private var listCountry: MutableList<Country> = mutableListOf()

    private val bag = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        bag.dispose()
    }

    fun subscribe(accountId: Int, categoryKey: String): LiveData<TopNewsState> {
        this.accountId = accountId
        loadNews(categoryKey)
        getAccountSettings()
        return flow
    }

    fun clickNews(article: Article) {
        if (!filterFlag) {
            if (accountId != ACCOUNT_ID_DEFAULT) {
                saveArticle(article)
            }
            mutableFlow.value = TopNewsState.ClickNews(article = article)
            if (article.isFavorites) mutableFlow.value = TopNewsState.FavoritesImageViewSetLike
            else mutableFlow.value = TopNewsState.FavoritesImageViewSetDislike
            mutableFlow.value = TopNewsState.HideFilterButton
            mutableFlow.value = TopNewsState.BottomSheetExpanded
        }
    }

    fun clickImageFavorites(article: Article) {
        if (article.isFavorites) {
            mutableFlow.value = TopNewsState.FavoritesImageViewSetDislike
            mutableFlow.value = TopNewsState.EventNavigationBarRemoveBadgeFavorites
            deleteFavorites(article)
            article.isFavorites = false
        } else {
            mutableFlow.value = TopNewsState.EventNavigationBarAddBadgeFavorites
            mutableFlow.value = TopNewsState.FavoritesImageViewSetLike
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

    fun filterButtonClick(country: String) {
        if (!filterFlag) {
            mutableFlow.value = TopNewsState.ShowFilterHideRecycler
            filterFlag = !filterFlag
        } else {
            if (country.isEmpty() || !listCountry.map { itemCountry -> itemCountry.id }
                    .contains(country)) {
                mutableFlow.value = TopNewsState.ErrorSelectCountry
            } else {
                val countryCode = listCountry.find { listCountry ->
                    listCountry.id == country
                }?.code ?: ALL_COUNTRY_VALUE
                sharedPreferenceAccount.setAccountCountryCode(countryCode)
                sharedPreferenceAccount.setAccountCountry(country)
                mutableFlow.value = TopNewsState.EventUpdateViewPager
                if (accountId != ACCOUNT_ID_DEFAULT) {
                    account.myCountry = country
                    account.countryCode = countryCode
                    accountRepoImpl.updateAccount(account.toAccountDbEntity()).subscribe({
                    }, { error ->
                        Log.d(ERROR_DB, error.localizedMessage)
                    })
                }
            }
        }
    }

    fun closeFilter() {
        filterFlag = !filterFlag
        mutableFlow.value = TopNewsState.HideFilterShowRecycler
    }

    fun behaviorCollapsed() {
        mutableFlow.value = TopNewsState.ShowFilterButton
    }

    fun getCountry() {
        countryRepoImpl.getCountry().subscribe({ listCountryDbEntity ->
            listCountry = listCountryDbEntity.map { countryDbEntity ->
                countryDbEntity.toCountry()
            } as MutableList<Country>
            listCountry.add(0, Country(ALL_COUNTRY, ALL_COUNTRY_VALUE))
            val list: MutableList<String> =
                listCountry.map { country -> country.id }.sortedBy { country -> country }
                    .toMutableList()
            var index = list.indexOf(sharedPreferenceAccount.getAccountCountry())
            if (index == -1) {
                index = 0
            }
            mutableFlow.value = TopNewsState.SetCountry(list, index)
        }, { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun saveArticle(article: Article) {
        if (saveHistory) {
            if (!article.isFavorites && !article.isHistory) {
                article.isHistory = true
                article.dateAdded = Date().formatDateTime()
                articleRepoImpl.insertArticle(article.toArticleDbEntity(accountId))
                    .subscribe({
                        articleListHistory.find { articleHistory ->
                            articleHistory.title == article.title
                        }?.isHistory = true
                        mutableFlow.value = TopNewsState.UpdateListNews(articleListHistory)
                    }, { error ->
                        Log.d(ERROR_DB, error.localizedMessage)
                    }).disposableBy(bag)
            }
        }
    }

    private fun saveArticleLike(article: Article) {
        val articleDbEntity = article.toArticleDbEntity(accountId)
        articleDbEntity.isFavorites = true
        articleRepoImpl.insertArticle(articleDbEntity).subscribe({
            articleListHistory.find { articleHistory ->
                articleHistory.title == article.title
            }?.isFavorites = true
            mutableFlow.value = TopNewsState.UpdateListNews(articleListHistory)
        }, {
        }).disposableBy(bag)
    }

    private fun deleteFavorites(article: Article) {
        article.isFavorites = false
        articleRepoImpl.deleteArticleByIdFavorites(article.title.toString(), accountId)
            .subscribe({
                articleListHistory.find { articleHistory ->
                    articleHistory.title == article.title
                }?.isFavorites = false
                mutableFlow.value = TopNewsState.UpdateListNews(articleListHistory)
            }, { error ->
                Log.d(ERROR_DB, error.localizedMessage)
            }).disposableBy(bag)
    }

    private fun getAccountSettings() {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            accountRepoImpl.getAccountById(accountId).subscribe({ accountDb ->
                account = accountDb
                saveHistory = accountDb.saveHistory
            }, { error ->
                Log.d(ERROR_DB, error.localizedMessage)
            }).disposableBy(bag)
        } else {
            mutableFlow.value = TopNewsState.HideFavoritesImageView
        }
    }

    private fun loadNews(category: String) {
        val countryCode = sharedPreferenceAccount.getAccountCountryCode()
        Single.zip(
            newsRepoImpl.getTopicalHeadlinesCategoryCountry(
                category = category,
                country = countryCode,
                key = API_KEY_NEWS),
            articleRepoImpl.getArticleById(accountId)) { news, articles ->
            val newsModified =
                news.articles.map { articleDto -> articleDto.toArticle() }.also { list ->
                    list.map { article ->
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
            newsModified.also { listArticle ->
                listArticle[0].viewType = BaseViewHolder.VIEW_TYPE_TOP_NEWS_HEADER
            }
            newsModified
        }.subscribeDefault().subscribe({ listArticle ->
            if (listArticle.isEmpty()) {
                mutableFlow.value = TopNewsState.EmptyListLoadNews
            } else {
                articleListHistory = listArticle.toMutableList()
                mutableFlow.value = TopNewsState.SetNews(listArticle)
            }
        }, { error ->
            mutableFlow.value = TopNewsState.ErrorLoadNews
            Log.d(ERROR_LOAD_NEWS, error.localizedMessage)
        }).disposableBy(bag)
    }

    sealed class TopNewsState {
        data class ClickNews(var article: Article) : TopNewsState()
        data class SetNews(var articles: List<Article>) : TopNewsState()
        data class SetCountry(var listCountry: List<String>, var index: Int) : TopNewsState()
        data class UpdateListNews(var articleListHistory: MutableList<Article>) : TopNewsState()

        object ErrorLoadNews : TopNewsState()
        object EmptyListLoadNews : TopNewsState()
        object ErrorSelectCountry : TopNewsState()

        object ShowFilterButton : TopNewsState()
        object HideFilterButton : TopNewsState()
        object ShowFilterHideRecycler : TopNewsState()
        object HideFilterShowRecycler : TopNewsState()
        object HideFavoritesImageView : TopNewsState()

        object FavoritesImageViewSetLike : TopNewsState()
        object FavoritesImageViewSetDislike : TopNewsState()
        object BottomSheetExpanded : TopNewsState()

        object EventNavigationBarAddBadgeFavorites : TopNewsState()//SkipStrategy activityEvent
        object EventNavigationBarRemoveBadgeFavorites : TopNewsState()//SkipStrategy activityEvent
        object EventUpdateViewPager : TopNewsState()
    }
}