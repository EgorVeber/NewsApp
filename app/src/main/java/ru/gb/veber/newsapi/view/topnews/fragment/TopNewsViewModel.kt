package ru.gb.veber.newsapi.view.topnews.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import retrofit2.HttpException
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
import ru.gb.veber.newsapi.utils.mapper.mapToArticleDbEntity
import ru.gb.veber.newsapi.utils.mapper.toAccountDbEntity
import ru.gb.veber.newsapi.utils.mapper.toArticle
import ru.gb.veber.newsapi.utils.mapper.toArticleUI
import ru.gb.veber.newsapi.utils.mapper.toCountry
import ru.gb.veber.newsapi.utils.subscribeDefault
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.BaseViewHolder
import java.util.*
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
            val newsModified = news.articles.map { articleDto ->
                articleDto.toArticle()
            }.also { list ->
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
                mutableFlow.value = TopNewsState.EmptyList
            } else {
                articleListHistory = listArticle.toMutableList()
                mutableFlow.value = TopNewsState.SetNews(listArticle)
            }
        }, { error ->
            if (error is HttpException){
                mutableFlow.value = TopNewsState.ErrorLoadNews
            }

            Log.d(ERROR_LOAD_NEWS, error.localizedMessage)
            Log.d(ERROR_LOAD_NEWS, error.message.toString())
            Log.d(ERROR_LOAD_NEWS, error.javaClass.name)
            Log.d(ERROR_LOAD_NEWS, error.javaClass.canonicalName)
        }).disposableBy(bag)
    }

    fun clickNews(article: Article) {
        if (!filterFlag) {
            if (accountId != ACCOUNT_ID_DEFAULT) {
                saveArticle(article)
            }
            mutableFlow.value = TopNewsState.ClickNews(article)
            if (article.isFavorites) mutableFlow.value = TopNewsState.FavoritesIvSetLike
            else mutableFlow.value = TopNewsState.FavoritesIvSetDislike
            mutableFlow.value = TopNewsState.HideFilterCountry
            mutableFlow.value = TopNewsState.BottomSheetExpanded
        }
    }

    private fun saveArticle(article: Article) {
        if (saveHistory) {
            if (!article.isFavorites && !article.isHistory) {
                article.isHistory = true
                article.dateAdded = Date().formatDateTime()
                articleRepoImpl.insertArticle(mapToArticleDbEntity(article, accountId))
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
        var item = mapToArticleDbEntity(article, accountId)
        item.isFavorites = true
        articleRepoImpl.insertArticle(item).subscribe({
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

    fun setOnClickImageFavorites(article: Article) {
        if (article.isFavorites) {
            mutableFlow.value = TopNewsState.FavoritesIvSetDislike
            mutableFlow.value = TopNewsState.NavigationBarRemoveBadgeFavorites
            deleteFavorites(article)
            article.isFavorites = false
        } else {
            mutableFlow.value = TopNewsState.NavigationBarAddBadgeFavorites
            mutableFlow.value = TopNewsState.FavoritesIvSetLike
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
            mutableFlow.value = TopNewsState.FadeRecyclerShowCountry
            filterFlag = !filterFlag
        } else {
            if (country.isEmpty() || !listCountry.map { itemCountry -> itemCountry.id }
                    .contains(country)) {
                mutableFlow.value = TopNewsState.ErrorCountry
            } else {
                var countryCode = listCountry.find { listCountry ->
                    listCountry.id == country
                }?.code ?: ALL_COUNTRY_VALUE
                sharedPreferenceAccount.setAccountCountryCode(countryCode)
                sharedPreferenceAccount.setAccountCountry(country)
                mutableFlow.value = TopNewsState.UpdateViewPagerEvent
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

    fun cancelFilter() {
        filterFlag = !filterFlag
        mutableFlow.value = TopNewsState.SetAlfaCancel
        mutableFlow.value = TopNewsState.SetImageFilterButtonCancel
        mutableFlow.value = TopNewsState.HideCountryList
        mutableFlow.value = TopNewsState.HideCancelButton
    }

    fun behaviorCollapsed() {
        mutableFlow.value = TopNewsState.ShowFilterCountry
    }

    fun getCountry() {
        countryRepoImpl.getCountry().subscribe({ country ->
            listCountry = country.map { countryDbEntity ->
                countryDbEntity.toCountry()
            } as MutableList<Country>
            listCountry.add(0, Country(ALL_COUNTRY, ALL_COUNTRY_VALUE))
            var list: MutableList<String> =
                listCountry.map { country -> country.id }
                    .sortedBy { country -> country } as MutableList<String>
            var index = list.indexOf(sharedPreferenceAccount.getAccountCountry())
            if (index == -1) {
                index = 0
            }
            mutableFlow.value = TopNewsState.SetCountry(list, index)
        }, { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    sealed class TopNewsState {
        data class SetNews(var articles: List<Article>) : TopNewsState()
        data class ClickNews(var article: Article) : TopNewsState()
        data class SetCountry(var listCountry: List<String>, var index: Int) : TopNewsState()
        data class UpdateListNews(var articleListHistory: MutableList<Article>) : TopNewsState()
        object HideFavoritesImageView : TopNewsState()
        object EmptyList : TopNewsState()
        object ErrorLoadNews : TopNewsState()
        object FavoritesIvSetLike : TopNewsState()
        object FavoritesIvSetDislike : TopNewsState()
        object NavigationBarAddBadgeFavorites : TopNewsState()//SkipStrategy activityEvent
        object NavigationBarRemoveBadgeFavorites : TopNewsState()//SkipStrategy activityEvent
        object BottomSheetExpanded : TopNewsState()
        object ShowFilterCountry : TopNewsState()
        object HideFilterCountry : TopNewsState()
        object SetAlfaCancel : TopNewsState()
        object SetImageFilterButtonCancel : TopNewsState()
        object HideCountryList : TopNewsState()
        object HideCancelButton : TopNewsState()
        object FadeRecyclerShowCountry : TopNewsState()
        object ErrorCountry : TopNewsState()
        object UpdateViewPagerEvent : TopNewsState()
    }
}