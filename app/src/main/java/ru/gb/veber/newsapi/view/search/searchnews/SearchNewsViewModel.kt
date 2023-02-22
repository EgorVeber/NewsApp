package ru.gb.veber.newsapi.view.search.searchnews

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.HistorySelect
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.database.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.model.repository.network.NewsRepo
import ru.gb.veber.newsapi.model.repository.room.AccountRepo
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepo
import ru.gb.veber.newsapi.model.repository.room.ArticleRepo
import ru.gb.veber.newsapi.model.repository.room.SourcesRepo
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.API_KEY_NEWS
import ru.gb.veber.newsapi.utils.ERROR_DB
import ru.gb.veber.newsapi.utils.disposableBy
import ru.gb.veber.newsapi.utils.formatDateTime
import ru.gb.veber.newsapi.utils.mapper.toArticle
import ru.gb.veber.newsapi.utils.mapper.toArticleDbEntity
import ru.gb.veber.newsapi.utils.mapper.toArticleUI
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.BaseViewHolder
import java.util.*
import javax.inject.Inject

class SearchNewsViewModel @Inject constructor(
    private val router: Router,
    private val accountRepo: AccountRepo,
    private val sourcesRepo: SourcesRepo,
    private val accountSourcesRepo: AccountSourcesRepo,
    private val articleRepo: ArticleRepo,
    private val newsRepo: NewsRepo
) : ViewModel() {

    private val mutableFlow: MutableLiveData<SearchNewsState> = MutableLiveData()
    private val flow: LiveData<SearchNewsState> = mutableFlow

    private var saveHistory = false

    private var articleListHistory: MutableList<Article> = mutableListOf()
    private var likeSources: MutableList<Sources> = mutableListOf()
    private var allSources: List<Sources> = listOf()

    private var sourcesID: Int = 0
    private val bag = CompositeDisposable()

    private var accountId = ACCOUNT_ID_DEFAULT

    override fun onCleared() {
        super.onCleared()
        bag.dispose()
    }

    fun subscribe(accountId: Int): LiveData<SearchNewsState> {
        this.accountId = accountId
        return flow
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun getAccountSettings() {
        accountRepo.getAccountById(accountId).subscribe({
            saveHistory = it.saveHistory
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
        if (accountId == ACCOUNT_ID_DEFAULT) {
            mutableFlow.value = SearchNewsState.HideFavorites
        } else {
            getSourcesLike()
            getSources()
        }
    }

    fun openScreenWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
    }

    fun exit() {
        router.exit()
    }

    fun getNews(historySelect: HistorySelect?) {

        mutableFlow.value = SearchNewsState.SetTitle(
            keyWord = historySelect?.keyWord,
            sourcesId = historySelect?.sourcesName,
            sortType = if (!historySelect?.keyWord.isNullOrEmpty()) historySelect?.sortByKeyWord else historySelect?.sortBySources,
            dateSources = historySelect?.dateSources
        )


        Single.zip(
            newsRepo.getEverythingKeyWordSearchInSources(
                sources = historySelect?.sourcesId,
                q = historySelect?.keyWord,
                searchIn = historySelect?.searchIn,
                sortBy = if (!historySelect?.keyWord.isNullOrEmpty()) historySelect?.sortByKeyWord else historySelect?.sortBySources,
                from = historySelect?.dateSources,
                to = historySelect?.dateSources,
                key = API_KEY_NEWS
            ).map { articles ->
                articles.articles.map { articleDto ->
                    articleDto.toArticle()
                }.also { list ->
                    list.map { article ->
                        article.toArticleUI()
                    }
                    list.map { art ->
                        art.viewType = BaseViewHolder.VIEW_TYPE_SEARCH_NEWS
                    }
                }
            }, articleRepo.getArticleById(accountId)
        ) { news, articles ->

            articles.forEach { art ->
                news.forEach { new ->
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
            news
        }.subscribe({
            if (it.isEmpty()) {
                mutableFlow.value = SearchNewsState.EmptyList
            } else {
                articleListHistory = it.toMutableList()
                mutableFlow.value = SearchNewsState.SetNews(it)
            }
        }, {
            mutableFlow.value = SearchNewsState.EmptyList
        }).disposableBy(bag)
    }

    fun setOnClickImageFavorites(article: Article) {
        if (article.isFavorites) {
            mutableFlow.value = SearchNewsState.SetLikeResourcesNegative
            mutableFlow.value = SearchNewsState.RemoveBadge

            deleteFavorites(article)
            article.isFavorites = false

        } else {
            mutableFlow.value = SearchNewsState.AddBadge
            mutableFlow.value = SearchNewsState.SetLikeResourcesActive

            saveArticleLike(article)
            article.isFavorites = true
        }
    }

    fun clickNews(article: Article) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            val isLikeSources = likeSources.find { it.idSources == article.source.id }?.id ?: 0
            sourcesID = allSources.find { it.idSources == article.source.id }?.id ?: 0

            if (isLikeSources != 0 || sourcesID == 0) {
                mutableFlow.value = SearchNewsState.HideSaveSources
            } else {
                mutableFlow.value = SearchNewsState.ShowSaveSources
            }
            saveArticle(article)
        }
        mutableFlow.value = SearchNewsState.ClickNews(article)
        if (article.isFavorites) {
            mutableFlow.value = SearchNewsState.SetLikeResourcesActive
        } else {
            mutableFlow.value = SearchNewsState.SetLikeResourcesNegative
        }
        mutableFlow.value = SearchNewsState.SheetExpanded
    }


    fun saveSources() {
        accountSourcesRepo.insert(AccountSourcesDbEntity(accountId, sourcesID)).subscribe({
            mutableFlow.value = SearchNewsState.SuccessSaveSources
            getSourcesLike()
        }, {
        }).disposableBy(bag)
    }


    private fun saveArticle(article: Article) {
        if (saveHistory) {
            if (!article.isFavorites && !article.isHistory) {
                article.isHistory = true
                article.dateAdded = Date().formatDateTime()
                articleRepo.insertArticle(article.toArticleDbEntity(accountId))
                    .subscribe({
                        articleListHistory.find { it.title == article.title }?.isHistory = true
                        mutableFlow.value = SearchNewsState.ChangeNews(articleListHistory)
                    }, {
                        Log.d(ERROR_DB, it.localizedMessage)
                    }).disposableBy(bag)
            }
        }
    }

    private fun deleteFavorites(article: Article) {
        article.isFavorites = false
        articleRepo.deleteArticleByIdFavorites(article.title.toString(), accountId).subscribe({
            articleListHistory.find { it.title == article.title }?.isFavorites = false
            mutableFlow.value = SearchNewsState.ChangeNews(articleListHistory)
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        }).disposableBy(bag)
    }

    private fun saveArticleLike(article: Article) {
        val item = article.toArticleDbEntity(accountId)
        item.isFavorites = true
        articleRepo.insertArticle(item).subscribe({
            articleListHistory.find { it.title == article.title }?.isFavorites = true
            mutableFlow.value = SearchNewsState.ChangeNews(articleListHistory)
        }, {
        }).disposableBy(bag)
    }

    private fun getSourcesLike() {
        accountSourcesRepo.getLikeSourcesFromAccount(accountId).subscribe({
            likeSources = it.toMutableList()
        }, {
        }).disposableBy(bag)
    }

    private fun getSources() {
        sourcesRepo.getSources().subscribe({
            allSources = it
        }, {
        }).disposableBy(bag)
    }

    sealed class SearchNewsState {
        data class SetNews(val list: List<Article>) : SearchNewsState()
        data class ChangeNews(val articleListHistory: List<Article>) : SearchNewsState()
        data class ClickNews(val article: Article) : SearchNewsState()
        data class SetTitle(
            val keyWord: String?,
            val sourcesId: String?,
            val sortType: String?,
            val dateSources: String?
        ) : SearchNewsState()

        object EmptyList : SearchNewsState()
        object HideFavorites : SearchNewsState()
        object HideSaveSources : SearchNewsState()
        object SetLikeResourcesActive : SearchNewsState()
        object AddBadge : SearchNewsState()
        object SetLikeResourcesNegative : SearchNewsState()
        object RemoveBadge : SearchNewsState()
        object SuccessSaveSources : SearchNewsState()
        object ShowSaveSources : SearchNewsState()
        object SheetExpanded : SearchNewsState()
    }
}
