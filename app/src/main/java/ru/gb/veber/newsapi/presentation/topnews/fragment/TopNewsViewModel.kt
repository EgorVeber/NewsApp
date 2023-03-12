package ru.gb.veber.newsapi.presentation.topnews.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.core.NewsViewModel
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.domain.interactor.TopNewsInteractor
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.Country
import ru.gb.veber.newsapi.core.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.core.utils.ALL_COUNTRY_VALUE
import ru.gb.veber.newsapi.core.utils.API_KEY_NEWS
import ru.gb.veber.newsapi.core.utils.ERROR_DB
import ru.gb.veber.newsapi.core.utils.ERROR_LOAD_NEWS
import ru.gb.veber.newsapi.core.utils.extentions.formatDateTime
import ru.gb.veber.newsapi.core.utils.extentions.launchJob
import ru.gb.veber.newsapi.data.mapper.toAccountDbEntity
import ru.gb.veber.newsapi.data.mapper.toArticle
import ru.gb.veber.newsapi.data.mapper.toArticleDbEntity
import ru.gb.veber.newsapi.data.mapper.toArticleUI
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder
import java.util.Date
import javax.inject.Inject

class TopNewsViewModel @Inject constructor(
    private val router: Router,
    private val topNewsInteractor: TopNewsInteractor,
) : NewsViewModel() {

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

    override fun onBackPressedRouter(): Boolean {
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

                topNewsInteractor.setAccountCountryCode(countryCode)
                topNewsInteractor.setAccountCountry(country)

                if (accountId != ACCOUNT_ID_DEFAULT) {
                    account.myCountry = country
                    account.countryCode = countryCode

                    viewModelScope.launchJob(tryBlock = {
                        topNewsInteractor.updateAccountV2(account.toAccountDbEntity())
                    }, catchBlock = { error ->
                        Log.d(ERROR_DB, error.localizedMessage)
                    }, finallyBlock = {
                        mutableFlow.postValue(TopNewsState.EventUpdateViewPager)
                    })
                } else {
                    mutableFlow.postValue(TopNewsState.EventUpdateViewPager)
                }
            }
        }
    }

    fun closeFilter(country: String) {
        if (country.isEmpty() || !listCountry.map { itemCountry -> itemCountry.id }
                .contains(country)) {
            mutableFlow.value = TopNewsState.ErrorSelectCountry
        } else {
            filterFlag = !filterFlag
            mutableFlow.value = TopNewsState.HideFilterShowRecycler
        }
    }

    fun behaviorCollapsed() {
        mutableFlow.value = TopNewsState.ShowFilterButton
    }

    fun getCountry() {
        viewModelScope.launchJob(tryBlock = {
            val result = topNewsInteractor.getCountry(listCountry)
            listCountry = result.third
            mutableFlow.postValue(TopNewsState.SetCountry(result.first, result.second))
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
                    val newList =
                        topNewsInteractor.insertArticleV2(
                            article.toArticleDbEntity(accountId),
                            articleListHistory,
                            article.title
                        )

                    articleListHistory = newList
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
            val newList =
                topNewsInteractor.insertArticleV2(
                    article.toArticleDbEntity(accountId),
                    articleListHistory,
                    article.title
                )

            articleListHistory = newList

            mutableFlow.postValue(TopNewsState.UpdateListNews(articleListHistory))
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun deleteFavorites(article: Article) {
        article.isFavorites = false
        viewModelScope.launchJob(tryBlock = {
            topNewsInteractor.deleteArticleByIdFavoritesV2(article.title.toString(), accountId)
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
                account = topNewsInteractor.getAccountByIdV2(accountId)
                saveHistory = account.saveHistory
            }, catchBlock = { error ->
                Log.d(ERROR_DB, error.localizedMessage)
            })
        } else {
            mutableFlow.value = TopNewsState.HideFavoritesImageView
        }
    }

    private fun loadNews(category: String) {
        val countryCode = topNewsInteractor.getAccountCountryCode()
        viewModelScope.launchJob(tryBlock = {
            var newsDto = topNewsInteractor.getTopicalHeadlinesCategoryCountryV2(
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
                var articles = topNewsInteractor.getArticleByIdV2(accountId)
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
