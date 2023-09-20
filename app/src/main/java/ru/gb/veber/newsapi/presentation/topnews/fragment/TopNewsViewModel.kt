package ru.gb.veber.newsapi.presentation.topnews.fragment

import android.util.Log
import androidx.core.util.toAndroidPair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.domain.interactor.TopNewsInteractor
import ru.gb.veber.newsapi.domain.models.AccountModel
import ru.gb.veber.newsapi.domain.models.CountryModel
import ru.gb.veber.newsapi.presentation.base.NewsViewModel
import ru.gb.veber.newsapi.presentation.mapper.toArticleModel
import ru.gb.veber.newsapi.presentation.mapper.toArticleUiModel
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import ru.gb.veber.ui_common.ALL_COUNTRY
import ru.gb.veber.ui_common.ALL_COUNTRY_VALUE
import ru.gb.veber.ui_common.API_KEY_NEWS
import ru.gb.veber.ui_common.TAG_DB_ERROR
import ru.gb.veber.ui_common.TAG_LOAD_NEWS_ERROR
import ru.gb.veber.ui_common.coroutine.launchJob
import ru.gb.veber.ui_common.utils.DateFormatter.toStringFormatDateYearMonthDay
import java.util.Date
import javax.inject.Inject
import kotlin.random.Random

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

    private val mapAnal: MutableMap<Int, MutableMap<Int, Boolean>> = mutableMapOf()
    private val mapAnal2: MutableMap<Boolean, MutableMap<Int, Int>> = mutableMapOf()

    fun subscribe(accountId: Int, categoryKey: String): LiveData<TopNewsState> {
        this.accountId = accountId
        loadNews(categoryKey)
        getAccountSettings()
        return flow
    }

    fun clickNews(articleModel: ArticleUiModel) {
        Log.d("mapAnal", "Do = $mapAnal")

        val period = Random.nextInt(0, 4)
        val event = Random.nextInt(0, 11)

        //val currentEventList: MutableList<Pair<Int, Boolean>> = mapAnal[period] ?: mutableListOf()
        //val availableEvents: Pair<Int, Boolean>? = currentEventList.find { it.first == event }
        //if (availableEvents != null) {
        //    currentEventList.remove(availableEvents)
        //    if (currentEventList.isEmpty()) {
        //        mapAnal.remove(period)
        //    } else {
        //        mapAnal[period] = currentEventList
        //    }
        //} else {
        //    currentEventList += mutableListOf(event to Random.nextBoolean())
        //    mapAnal[period] = currentEventList
        //}

        val eventList: MutableMap<Int, Boolean> = mapAnal.getOrPut(period) { mutableMapOf() }
        if (eventList.contains(event)) {
            eventList.remove(event)
            if (eventList.isEmpty()) {
                mapAnal.remove(period)
            }
        } else {
            eventList += mutableMapOf(event to Random.nextBoolean())
        }


        Log.d("mapAnal", "Prepare = $mapAnal")
    }

    fun filterButtonClick(country: String) {
        // mapAnal.clear()
        // private val mapAnal: MutableMap<Int, MutableMap<Int, Boolean>> = mutableMapOf()
        val list = mapAnal.map {
            Period.getText(it.key) to it.value.filter { it.value }.keys.map { Event.getText(it) }
        }


        list.map { all ->
            all.second.forEach {
                Log.d("mapAnal", "subEvent period = " + all.first + " event = " + it)
            }
        }


        //  Log.d("mapAnal", "subscribe = " + subscribeList.toString())
        Log.d("", "unSubscribe = $list")
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
            Log.d(TAG_DB_ERROR, error.localizedMessage)
        })
    }

    private fun saveArticle(articleUiModel: ArticleUiModel) {
        if (accountId == ACCOUNT_ID_DEFAULT) return
        if (saveHistory) {
            if (!articleUiModel.isFavorites && !articleUiModel.isHistory) {
                articleUiModel.isHistory = true
                articleUiModel.dateAdded = Date().toStringFormatDateYearMonthDay()
                viewModelScope.launchJob(tryBlock = {
                    topNewsInteractor.insertArticle(articleUiModel.toArticleModel(), accountId)
                    articleModelListHistory.find { articleHistory ->
                        articleHistory.title == articleUiModel.title
                    }?.isHistory = true
                    mutableFlow.postValue(TopNewsState.UpdateListNews(articleModelListHistory))
                }, catchBlock = { error ->
                    Log.d(TAG_DB_ERROR, error.localizedMessage)
                })
            }
        }
    }

    private fun saveArticleLike(articleModel: ArticleUiModel) {
        articleModel.isFavorites = true
        viewModelScope.launchJob(tryBlock = {
            topNewsInteractor.insertArticle(articleModel.toArticleModel(), accountId)
            articleModelListHistory.find { articleHistory ->
                articleHistory.title == articleModel.title
            }?.isFavorites = true

            mutableFlow.postValue(TopNewsState.UpdateListNews(articleModelListHistory))
        }, catchBlock = { error ->
            Log.d(TAG_DB_ERROR, error.localizedMessage)
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
            Log.d(TAG_DB_ERROR, error.localizedMessage)
        })
    }

    private fun getAccountSettings() {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            viewModelScope.launchJob(tryBlock = {
                accountModel = topNewsInteractor.getAccount(accountId)
                saveHistory = accountModel.saveHistory
            }, catchBlock = { error ->
                Log.d(TAG_DB_ERROR, error.localizedMessage)
            })
        } else {
            mutableFlow.value = TopNewsState.HideFavoritesImageView
        }
    }

    private fun loadNews(category: String) {
        val countryCode = topNewsInteractor.getAccountCountryCode()
        viewModelScope.launchJob(tryBlock = {
            val articlesUiList: List<ArticleUiModel> =
                topNewsInteractor.getNews(category, countryCode, API_KEY_NEWS, accountId)
                    .map { it.toArticleUiModel() }.apply {
                        this[0].viewType = BaseViewHolder.VIEW_TYPE_TOP_NEWS_HEADER
                    }

            if (articlesUiList.isEmpty()) {
                mutableFlow.postValue(TopNewsState.EmptyListLoadNews)
                return@launchJob
            }

            articleModelListHistory = articlesUiList.toMutableList()
            mutableFlow.postValue(TopNewsState.SetNews(articlesUiList))

        }, catchBlock = { error ->
            mutableFlow.postValue(TopNewsState.ErrorLoadNews)
            Log.d(TAG_LOAD_NEWS_ERROR, error.localizedMessage)
        })
    }

    sealed class TopNewsState {
        data class ClickNews(var articleModel: ArticleUiModel) : TopNewsState()
        data class SetNews(var articleModels: List<ArticleUiModel>) : TopNewsState()
        data class SetCountry(var listCountry: List<String>, var index: Int) : TopNewsState()
        data class UpdateListNews(var articleModelListHistory: MutableList<ArticleUiModel>) :
            TopNewsState()

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

enum class Period(val id: Int) {
    TYPE0(0),
    TYPE1(1),
    TYPE2(2),
    TYPE3(3);

    companion object {
        fun getText(id: Int): String {
           return when (id) {
                TYPE0.id -> "TYPE0"
                TYPE1.id -> "TYPE1"
                TYPE2.id -> "TYPE2"
                TYPE3.id -> "TYPE3"
               else -> {"asdsadas"}
           }
        }
    }
}

enum class Event(val id: Int) {
    EVENT0(0),
    EVENT1(1),
    EVENT2(2),
    EVENT3(3),
    EVENT4(4),
    EVENT5(5),
    EVENT6(6),
    EVENT7(7),
    EVENT8(8);

    companion object {
        fun getText(id: Int): String {
           return when (id) {
                EVENT0.id -> "EVENT0"
                EVENT1.id -> "EVENT1"
                EVENT2.id -> "EVENT2"
                EVENT3.id -> "EVENT3"
                EVENT4.id -> "EVENT4"
                EVENT5.id -> "EVENT5"
                EVENT6.id -> "EVENT6"
                EVENT7.id -> "EVENT7"
                EVENT8.id -> "EVENT8"
               else -> {"EVENTasdasdas8"}
           }
        }
    }
}