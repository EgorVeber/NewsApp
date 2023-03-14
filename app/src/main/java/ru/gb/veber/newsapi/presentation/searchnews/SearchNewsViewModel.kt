package ru.gb.veber.newsapi.presentation.searchnews

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.gb.veber.newsapi.common.extentions.SingleSharedFlow
import ru.gb.veber.newsapi.common.extentions.formatDateTime
import ru.gb.veber.newsapi.common.extentions.launchJob
import ru.gb.veber.newsapi.common.screen.WebViewScreen
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.API_KEY_NEWS
import ru.gb.veber.newsapi.common.utils.ERROR_DB
import ru.gb.veber.newsapi.data.mapper.toArticle
import ru.gb.veber.newsapi.data.mapper.toArticleDbEntity
import ru.gb.veber.newsapi.data.mapper.toArticleUI
import ru.gb.veber.newsapi.data.models.room.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.domain.models.Article
import ru.gb.veber.newsapi.domain.models.HistorySelect
import ru.gb.veber.newsapi.domain.models.Sources
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.AccountSourcesRepo
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import ru.gb.veber.newsapi.domain.repository.NewsRepo
import ru.gb.veber.newsapi.domain.repository.SourcesRepo
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder
import java.util.*
import javax.inject.Inject

class SearchNewsViewModel @Inject constructor(
    private val router: Router,
    private val accountRepo: AccountRepo,
    private val sourcesRepo: SourcesRepo,
    private val accountSourcesRepo: AccountSourcesRepo,
    private val articleRepo: ArticleRepo,
    private val newsRepo: NewsRepo,
) : ViewModel() {

    private val searchNewsState: MutableSharedFlow<SearchNewsState> =
        MutableSharedFlow(replay = 2,
            extraBufferCapacity = 2,
            onBufferOverflow = BufferOverflow.SUSPEND)
    private val searchNewsFlow: SharedFlow<SearchNewsState> = searchNewsState.asSharedFlow()

    private val articleDataState: MutableSharedFlow<Article> = MutableSharedFlow(replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val articleDataFlow: SharedFlow<Article> = articleDataState.asSharedFlow()

    private val imageLikeState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val imageLikeFlow: StateFlow<Boolean> = imageLikeState.asStateFlow()

    private val saveSourcesState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val saveSourcesFlow: StateFlow<Boolean> = saveSourcesState.asStateFlow()

    private val showMessageState = SingleSharedFlow<Boolean>()
    val showMessageFlow = showMessageState.asSharedFlow()

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

    fun subscribe(accountId: Int): SharedFlow<SearchNewsState> {
        this.accountId = accountId
        return searchNewsFlow
    }

    fun openScreenWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
    }

    fun exit() {
        router.exit()
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun getAccountSettings(historySelect: HistorySelect) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            viewModelScope.launchJob(tryBlock = {
                accountRepo.getAccountByIdV2(accountId).also { account ->
                    saveHistory = account.saveHistory
                }
            }, catchBlock = { error ->
                Log.d(ERROR_DB, error.localizedMessage)
            })
            viewModelScope.launchJob(tryBlock = {
                getSourcesLike()
                getSources()
            }, catchBlock = { error ->
                Log.d(ERROR_DB, error.localizedMessage)
            })
        } else {
            searchNewsState.tryEmit(SearchNewsState.HideFavorites)
        }
        getNews(historySelect)
    }

    fun setOnClickImageFavorites(article: Article) {
        if (article.isFavorites) {
            imageLikeState.tryEmit(false)
            searchNewsState.tryEmit(SearchNewsState.RemoveBadge)
            deleteFavorites(article)
            article.isFavorites = false

        } else {
            searchNewsState.tryEmit(SearchNewsState.AddBadge)
            imageLikeState.tryEmit(true)
            saveArticleLike(article)
            article.isFavorites = true
        }
    }

    fun clickNews(article: Article) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            val isLikeSources =
                likeSources.find { sources -> sources.idSources == article.source.id }?.id ?: 0
            sourcesID =
                allSources.find { sources -> sources.idSources == article.source.id }?.id ?: 0

            if (isLikeSources != 0 || sourcesID == 0) {
                saveSourcesState.tryEmit(false)
            } else {
                saveSourcesState.tryEmit(true)
            }
            saveArticle(article)
        }

        articleDataState.tryEmit(article)

        if (article.isFavorites) {
            imageLikeState.tryEmit(true)
        } else {
            imageLikeState.tryEmit(false)
        }
        searchNewsState.tryEmit(SearchNewsState.SheetExpanded)
    }

    fun saveSources() {
        viewModelScope.launchJob(tryBlock = {
            accountSourcesRepo.insertV2(AccountSourcesDbEntity(accountId, sourcesID))
            showMessageState.emit(true)
            saveSourcesState.emit(false)
            getSourcesLike()
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun getNews(historySelect: HistorySelect) {
        viewModelScope.launchJob(tryBlock = {
            searchNewsState.tryEmit(SearchNewsState.SetTitle(historySelect))
            val articlesDto = newsRepo.getEverythingKeyWordSearchInSourcesV2(
                sources = historySelect.sourcesId,
                q = historySelect.keyWord,
                searchIn = historySelect.searchIn,
                sortBy = if (!historySelect.keyWord.isNullOrEmpty()) historySelect.sortByKeyWord else historySelect.sortBySources,
                from = historySelect.dateSources,
                to = historySelect.dateSources,
                key = API_KEY_NEWS
            )

            val articlesUi = articlesDto.articles.map { articleDto ->
                articleDto.toArticle()
            }.map { article ->
                article.toArticleUI()
                article.viewType = BaseViewHolder.VIEW_TYPE_SEARCH_NEWS
                article
            }

            val articlesDb = articleRepo.getArticleByIdV2(accountId)
            articlesDb.forEach { art ->
                articlesUi.forEach { new ->
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
            if (articlesUi.isEmpty()) {
                searchNewsState.tryEmit(SearchNewsState.EmptyList)
            } else {
                checkUpdateList(articlesUi.toMutableList()) {
                    articleListHistory = articlesUi.toMutableList()
                    searchNewsState.tryEmit(SearchNewsState.SetNews(articleListHistory))
                }
            }
        }, catchBlock = { error ->
            searchNewsState.tryEmit(SearchNewsState.EmptyList)
            Log.d(ERROR_DB, error.localizedMessage)
        }, finallyBlock = {
            searchNewsState.tryEmit(SearchNewsState.HideProgress)
        })
    }

    private fun checkUpdateList(newList: MutableList<Article>, action: () -> Unit) {
        if (articleListHistory != newList) {
            action()//избыточно но может будет переисользоватся поэтому оставил.
        }
    }

    private fun saveArticle(article: Article) {
        if (saveHistory) {
            val checkSaved = !article.isFavorites && !article.isHistory
            if (checkSaved) {
                article.isHistory = true
                article.dateAdded = Date().formatDateTime()
                viewModelScope.launchJob(tryBlock = {
                    articleRepo.insertArticleV2(article.toArticleDbEntity(accountId))
                    articleListHistory.find { articleHistory -> articleHistory.title == article.title }
                        ?.isHistory = true
                    searchNewsState.tryEmit(SearchNewsState.ChangeNews(articleListHistory))
                }, catchBlock = { error ->
                    Log.d(ERROR_DB, error.localizedMessage)
                })
            }
        }
    }

    private fun deleteFavorites(article: Article) {
        article.isFavorites = false
        viewModelScope.launchJob(tryBlock = {

            articleRepo.deleteArticleByIdFavoritesV2(article.title.toString(), accountId)

            articleListHistory.find { articleHistory -> articleHistory.title == article.title }
                ?.isFavorites = false

            searchNewsState.tryEmit(SearchNewsState.ChangeNews(articleListHistory))
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun saveArticleLike(article: Article) {
        val item = article.toArticleDbEntity(accountId)
        item.isFavorites = true
        viewModelScope.launchJob(tryBlock = {
            articleRepo.insertArticleV2(item)
            articleListHistory.find { articleHistory -> articleHistory.title == article.title }
                ?.isFavorites = true
            searchNewsState.tryEmit(SearchNewsState.ChangeNews(articleListHistory))

        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun getSourcesLike() {
        viewModelScope.launchJob(tryBlock = {
            likeSources = accountSourcesRepo.getLikeSourcesFromAccountV2(accountId).toMutableList()
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun getSources() {
        viewModelScope.launchJob(tryBlock = {
            allSources = sourcesRepo.getSourcesV2()
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    sealed class SearchNewsState {
        data class SetNews(val list: List<Article>) : SearchNewsState()
        data class ChangeNews(val articleListHistory: List<Article>) : SearchNewsState()
        data class SetTitle(val historySelect: HistorySelect) : SearchNewsState()
        object EmptyList : SearchNewsState()
        object HideFavorites : SearchNewsState()
        object HideProgress : SearchNewsState()
        object AddBadge : SearchNewsState()
        object RemoveBadge : SearchNewsState()
        object SheetExpanded : SearchNewsState()
        object StartedState : SearchNewsState()
    }
}
