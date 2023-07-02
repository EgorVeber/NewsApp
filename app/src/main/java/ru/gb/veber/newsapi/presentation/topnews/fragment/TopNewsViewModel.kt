package ru.gb.veber.newsapi.presentation.topnews.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.common.base.NewsViewModel
import ru.gb.veber.newsapi.common.extentions.DateFormatter.formatDate
import ru.gb.veber.newsapi.common.extentions.launchJob
import ru.gb.veber.newsapi.common.screen.WebViewScreen
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.ALL_COUNTRY
import ru.gb.veber.newsapi.common.utils.ALL_COUNTRY_VALUE
import ru.gb.veber.newsapi.common.utils.API_KEY_NEWS
import ru.gb.veber.newsapi.common.utils.ERROR_DB
import ru.gb.veber.newsapi.common.utils.ERROR_LOAD_NEWS
import ru.gb.veber.newsapi.domain.interactor.TopNewsInteractor
import ru.gb.veber.newsapi.domain.models.AccountModel
import ru.gb.veber.newsapi.domain.models.ArticleModel
import ru.gb.veber.newsapi.domain.models.CountryModel
import ru.gb.veber.newsapi.presentation.mapper.toArticleModel
import ru.gb.veber.newsapi.presentation.mapper.toArticleUiModel
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder
import java.util.Date
import javax.inject.Inject

class TopNewsViewModel @Inject constructor(
    private val router: Router,
    private val topNewsInteractor: TopNewsInteractor,
) : NewsViewModel() {

    private val mutableFlow: MutableLiveData<TopNewsState> = MutableLiveData()
    private val flow: LiveData<TopNewsState> = mutableFlow

    private lateinit var accountModel: AccountModel
    private var accountId: Int = 0

    private var saveHistory = false
    private var filterFlag = false

    private var articleModelListHistory: MutableList<ArticleUiModel> = mutableListOf()
    private var listCountryModel: MutableList<CountryModel> = mutableListOf()

    fun subscribe(accountId: Int, categoryKey: String): LiveData<TopNewsState> {
        this.accountId = accountId
        loadNews(categoryKey)
        getAccountSettings()
        return flow
    }

    fun clickNews(articleModel: ArticleUiModel) {
        if (!filterFlag) {
            if (accountId != ACCOUNT_ID_DEFAULT) {
                saveArticle(articleModel)
            }
            mutableFlow.value = TopNewsState.ClickNews(articleModel = articleModel)
            if (articleModel.isFavorites) mutableFlow.value = TopNewsState.FavoritesImageViewSetLike
            else mutableFlow.value = TopNewsState.FavoritesImageViewSetDislike
            mutableFlow.value = TopNewsState.HideFilterButton
            mutableFlow.value = TopNewsState.BottomSheetExpanded
        }
    }

    fun clickImageFavorites(articleModel: ArticleUiModel) {
        if (articleModel.isFavorites) {
            mutableFlow.value = TopNewsState.FavoritesImageViewSetDislike
            mutableFlow.value = TopNewsState.EventNavigationBarRemoveBadgeFavorites
            deleteFavorites(articleModel)
            articleModel.isFavorites = false
        } else {
            mutableFlow.value = TopNewsState.EventNavigationBarAddBadgeFavorites
            mutableFlow.value = TopNewsState.FavoritesImageViewSetLike
            saveArticleLike(articleModel)
            articleModel.isFavorites = true
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
            if (country.isEmpty() || !listCountryModel.map { itemCountry -> itemCountry.id }
                    .contains(country)) {
                mutableFlow.value = TopNewsState.ErrorSelectCountry
            } else {
                val countryCode = listCountryModel.find { listCountry ->
                    listCountry.id == country
                }?.code ?: ALL_COUNTRY_VALUE

                topNewsInteractor.setAccountCountryCode(countryCode)
                topNewsInteractor.setAccountCountry(country)

                if (accountId != ACCOUNT_ID_DEFAULT) {
                    accountModel.myCountry = country
                    accountModel.countryCode = countryCode

                    viewModelScope.launchJob(tryBlock = {
                        topNewsInteractor.updateAccount(accountModel)
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
        if (country.isEmpty() || !listCountryModel.map { itemCountry -> itemCountry.id }
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
            listCountryModel = topNewsInteractor.getCountry().toMutableList()
            listCountryModel.add(0, CountryModel(ALL_COUNTRY, ALL_COUNTRY_VALUE))
            val list: MutableList<String> =
                listCountryModel.map { country -> country.id }.sortedBy { country -> country }
                    .toMutableList()
            var index = list.indexOf(topNewsInteractor.getAccountCountry())
            if (index == -1) {
                index = 0
            }
            mutableFlow.postValue(TopNewsState.SetCountry(list, index))
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun saveArticle(articleModel: ArticleUiModel) {
        if (saveHistory) {
            if (!articleModel.isFavorites && !articleModel.isHistory) {
                articleModel.isHistory = true
                articleModel.dateAdded = Date().formatDate()
                viewModelScope.launchJob(tryBlock = {
                    topNewsInteractor.insertArticle(articleModel.toArticleModel(), accountId)
                    articleModelListHistory.find { articleHistory ->
                        articleHistory.title == articleModel.title
                    }?.isHistory = true
                    mutableFlow.postValue(TopNewsState.UpdateListNews(articleModelListHistory))
                }, catchBlock = { error ->
                    Log.d(ERROR_DB, error.localizedMessage)
                })
            }
        }
    }

    private fun saveArticleLike(articleModel: ArticleUiModel) {
        articleModel.isFavorites = true
        viewModelScope.launchJob(tryBlock = {
            topNewsInteractor.insertArticle(articleModel.toArticleModel(),accountId)
            articleModelListHistory.find { articleHistory ->
                articleHistory.title == articleModel.title
            }?.isFavorites = true

            mutableFlow.postValue(TopNewsState.UpdateListNews(articleModelListHistory))
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun deleteFavorites(articleModel: ArticleUiModel) {
        articleModel.isFavorites = false
        viewModelScope.launchJob(tryBlock = {
            topNewsInteractor.deleteArticleByIdFavoritesV2(articleModel.title.toString(), accountId)
            articleModelListHistory.find { articleHistory ->
                articleHistory.title == articleModel.title
            }?.isFavorites = false
            mutableFlow.postValue(TopNewsState.UpdateListNews(articleModelListHistory))
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun getAccountSettings() {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            viewModelScope.launchJob(tryBlock = {
                accountModel = topNewsInteractor.getAccountByIdV2(accountId)
                saveHistory = accountModel.saveHistory
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
            val articleList: List<ArticleModel> = topNewsInteractor.getTopicalHeadlinesCategoryCountryV2(
                category = category,
                country = countryCode,
                key = API_KEY_NEWS
            )

            val articleUiList = articleList.map { article->article.toArticleUiModel() }

            //TODO Вынести в домайн
            if (accountId != ACCOUNT_ID_DEFAULT) {
                val articles = topNewsInteractor.getArticleById(accountId)
                articles.forEach { art ->
                    articleUiList.forEach { new ->
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
            articleUiList.also { listArticle ->
                listArticle[0].viewType = BaseViewHolder.VIEW_TYPE_TOP_NEWS_HEADER
            }

            if (articleUiList.isEmpty()) {
                mutableFlow.postValue(TopNewsState.EmptyListLoadNews)
            } else {
                articleModelListHistory = articleUiList.toMutableList()
                mutableFlow.postValue(TopNewsState.SetNews(articleUiList))
            }
        }, catchBlock = { error ->
            mutableFlow.postValue(TopNewsState.ErrorLoadNews)
            Log.d(ERROR_LOAD_NEWS, error.localizedMessage)
        })
    }

    sealed class TopNewsState {
        data class ClickNews(var articleModel: ArticleUiModel) : TopNewsState()
        data class SetNews(var articleModels: List<ArticleUiModel>) : TopNewsState()
        data class SetCountry(var listCountry: List<String>, var index: Int) : TopNewsState()
        data class UpdateListNews(var articleModelListHistory: MutableList<ArticleUiModel>) : TopNewsState()

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

        object EventNavigationBarAddBadgeFavorites : TopNewsState()
        object EventNavigationBarRemoveBadgeFavorites : TopNewsState()
        object EventUpdateViewPager : TopNewsState()
    }
}
