package ru.gb.veber.newsapi.view.topnews.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
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
import ru.gb.veber.newsapi.utils.extentions.formatDateTime
import ru.gb.veber.newsapi.utils.extentions.launchJob
import ru.gb.veber.newsapi.utils.mapper.toAccountDbEntity
import ru.gb.veber.newsapi.utils.mapper.toArticle
import ru.gb.veber.newsapi.utils.mapper.toArticleDbEntity
import ru.gb.veber.newsapi.utils.mapper.toArticleUI
import ru.gb.veber.newsapi.utils.mapper.toCountry
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

                if (accountId != ACCOUNT_ID_DEFAULT) {
                    account.myCountry = country
                    account.countryCode = countryCode

                    viewModelScope.launchJob(tryBlock = {
                        accountRepoImpl.updateAccountV2(account.toAccountDbEntity())
                    }, catchBlock = { error ->
                        Log.d(ERROR_DB, error.localizedMessage)
                    }, finallyBlock = {
                        mutableFlow.postValue(TopNewsState.EventUpdateViewPager)
                    })
                } else{
                    mutableFlow.postValue(TopNewsState.EventUpdateViewPager)
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
        viewModelScope.launchJob(tryBlock = {
            var listCountryDbEntity = countryRepoImpl.getCountryV2()
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
            mutableFlow.postValue(TopNewsState.SetCountry(list, index))
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun saveArticle(article: Article) {
        if (saveHistory) {
            if (!article.isFavorites && !article.isHistory) {
                article.isHistory = true
                article.dateAdded = Date().formatDateTime()
                viewModelScope.launchJob(tryBlock = {
                    articleRepoImpl.insertArticleV2(article.toArticleDbEntity(accountId))
                    articleListHistory.find { articleHistory ->
                        articleHistory.title == article.title
                    }?.isHistory = true
                    mutableFlow.postValue(TopNewsState.UpdateListNews(articleListHistory))
                }, catchBlock = { error ->
                    Log.d(ERROR_DB, error.localizedMessage)
                })
            }
        }
    }

    private fun saveArticleLike(article: Article) {
        val articleDbEntity = article.toArticleDbEntity(accountId)
        articleDbEntity.isFavorites = true

        viewModelScope.launchJob(tryBlock = {
            articleRepoImpl.insertArticleV2(articleDbEntity)
            articleListHistory.find { articleHistory ->
                articleHistory.title == article.title
            }?.isFavorites = true

            mutableFlow.postValue(TopNewsState.UpdateListNews(articleListHistory))
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun deleteFavorites(article: Article) {
        article.isFavorites = false
        viewModelScope.launchJob(tryBlock = {
            articleRepoImpl.deleteArticleByIdFavoritesV2(article.title.toString(), accountId)
            articleListHistory.find { articleHistory ->
                articleHistory.title == article.title
            }?.isFavorites = false
            mutableFlow.postValue(TopNewsState.UpdateListNews(articleListHistory))
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun getAccountSettings() {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            viewModelScope.launchJob(tryBlock = {
                account = accountRepoImpl.getAccountByIdV2(accountId)
                saveHistory = account.saveHistory
            }, catchBlock = { error ->
                Log.d(ERROR_DB, error.localizedMessage)
            })
        } else {
            mutableFlow.value = TopNewsState.HideFavoritesImageView
        }
    }

    private fun loadNews(category: String) {
        val countryCode = sharedPreferenceAccount.getAccountCountryCode()
        viewModelScope.launchJob(tryBlock = {
            var newsDto = newsRepoImpl.getTopicalHeadlinesCategoryCountryV2(
                category = category,
                country = countryCode,
                key = API_KEY_NEWS
            )

            val newsUI =
                newsDto.articles.map { articleDto -> articleDto.toArticle() }.also { list ->
                    list.map { article ->
                        article.toArticleUI()
                    }
                }
            if (accountId != ACCOUNT_ID_DEFAULT) {
                var articles = articleRepoImpl.getArticleByIdV2(accountId)
                articles.forEach { art ->
                    newsUI.forEach { new ->
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
            }
            newsUI.also { listArticle ->
                listArticle[0].viewType = BaseViewHolder.VIEW_TYPE_TOP_NEWS_HEADER
            }

            if (newsUI.isEmpty()) {
                mutableFlow.postValue(TopNewsState.EmptyListLoadNews)
            } else {
                articleListHistory = newsUI.toMutableList()
                mutableFlow.postValue(TopNewsState.SetNews(newsUI))
            }
        }, catchBlock = { error ->
            mutableFlow.postValue(TopNewsState.ErrorLoadNews)
            Log.d(ERROR_LOAD_NEWS, error.localizedMessage)
        })
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