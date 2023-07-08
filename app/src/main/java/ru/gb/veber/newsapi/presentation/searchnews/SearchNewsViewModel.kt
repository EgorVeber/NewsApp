package ru.gb.veber.newsapi.presentation.searchnews

import android.util.Log
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
import ru.gb.veber.newsapi.common.base.NewsViewModel
import ru.gb.veber.newsapi.common.extentions.DateFormatter.toStringFormatDateDefault
import ru.gb.veber.newsapi.common.extentions.SingleSharedFlow
import ru.gb.veber.newsapi.common.extentions.launchJob
import ru.gb.veber.newsapi.common.screen.WebViewScreen
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.API_KEY_NEWS
import ru.gb.veber.newsapi.common.utils.ERROR_DB
import ru.gb.veber.newsapi.domain.interactor.SearchNewsInteractor
import ru.gb.veber.newsapi.domain.models.AccountSourcesModel
import ru.gb.veber.newsapi.domain.models.ArticleModel
import ru.gb.veber.newsapi.domain.models.HistorySelectModel
import ru.gb.veber.newsapi.domain.models.SourcesModel
import ru.gb.veber.newsapi.presentation.mapper.toArticleModel
import ru.gb.veber.newsapi.presentation.mapper.toArticleUiModel
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder
import java.util.Date
import javax.inject.Inject

class SearchNewsViewModel @Inject constructor(
    private val router: Router,
    private val searchNewsInteractor: SearchNewsInteractor,
) : NewsViewModel() {

    private val searchNewsState: MutableSharedFlow<SearchNewsState> =
        MutableSharedFlow(
            replay = 2,
            extraBufferCapacity = 2,
            onBufferOverflow = BufferOverflow.SUSPEND
        )
    private val searchNewsFlow: SharedFlow<SearchNewsState> = searchNewsState.asSharedFlow()

    private val articleModelDataState: MutableSharedFlow<ArticleUiModel> = MutableSharedFlow(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val articleModelDataFlow: SharedFlow<ArticleUiModel> = articleModelDataState.asSharedFlow()

    private val imageLikeState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val imageLikeFlow: StateFlow<Boolean> = imageLikeState.asStateFlow()

    private val saveSourcesState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val saveSourcesFlow: StateFlow<Boolean> = saveSourcesState.asStateFlow()

    private val showMessageState = SingleSharedFlow<Boolean>()
    val showMessageFlow = showMessageState.asSharedFlow()

    private var saveHistory = false

    private var articleModelListHistory: MutableList<ArticleUiModel> = mutableListOf()
    private var likeSources: MutableList<SourcesModel> = mutableListOf()
    private var allSources: List<SourcesModel> = listOf()

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

    override fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun getAccountSettings(historySelectModel: HistorySelectModel) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            viewModelScope.launchJob(tryBlock = {
                searchNewsInteractor.getAccount(accountId).also { account ->
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
        getNews(historySelectModel)
    }

    fun setOnClickImageFavorites(articleModel: ArticleUiModel) {
        if (articleModel.isFavorites) {
            imageLikeState.tryEmit(false)
            searchNewsState.tryEmit(SearchNewsState.RemoveBadge)
            deleteFavorites(articleModel)
            articleModel.isFavorites = false

        } else {
            searchNewsState.tryEmit(SearchNewsState.AddBadge)
            imageLikeState.tryEmit(true)
            saveArticleLike(articleModel)
            articleModel.isFavorites = true
        }
    }

    fun clickNews(articleModel: ArticleUiModel) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            val isLikeSources =
                likeSources.find { sources -> sources.idSources == articleModel.sourceModel.id }?.id
                    ?: 0
            sourcesID =
                allSources.find { sources -> sources.idSources == articleModel.sourceModel.id }?.id
                    ?: 0

            if (isLikeSources != 0 || sourcesID == 0) {
                saveSourcesState.tryEmit(false)
            } else {
                saveSourcesState.tryEmit(true)
            }
            saveArticle(articleModel)
        }

        articleModelDataState.tryEmit(articleModel)

        if (articleModel.isFavorites) {
            imageLikeState.tryEmit(true)
        } else {
            imageLikeState.tryEmit(false)
        }
        searchNewsState.tryEmit(SearchNewsState.SheetExpanded)
    }

    fun saveSources() {
        viewModelScope.launchJob(tryBlock = {
            searchNewsInteractor.insertSource(AccountSourcesModel(accountId, sourcesID))
            showMessageState.emit(true)
            saveSourcesState.emit(false)
            getSourcesLike()
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun getNews(historySelectModel: HistorySelectModel) {
        //TODO унести в домайн
        viewModelScope.launchJob(tryBlock = {
            searchNewsState.tryEmit(SearchNewsState.SetTitle(historySelectModel))
            val articlesList: List<ArticleModel> = searchNewsInteractor.getNews(
                sources = historySelectModel.sourcesId,
                q = historySelectModel.keyWord,
                searchIn = historySelectModel.searchIn,
                sortBy = if (!historySelectModel.keyWord.isNullOrEmpty()) historySelectModel.sortByKeyWord else historySelectModel.sortBySources,
                from = historySelectModel.dateSources,
                to = historySelectModel.dateSources,
                key = API_KEY_NEWS
            )

            val articlesUiList: List<ArticleUiModel> =
                articlesList.map { article -> article.toArticleUiModel(viewType = BaseViewHolder.VIEW_TYPE_SEARCH_NEWS) }

            val articlesDb = searchNewsInteractor.getArticles(accountId)
            articlesDb.forEach { art ->
                articlesUiList.forEach { new ->
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

            if (articlesUiList.isEmpty()) {
                searchNewsState.tryEmit(SearchNewsState.EmptyList)
            } else {
                checkUpdateList(articlesUiList) {
                    articleModelListHistory = articlesUiList.toMutableList()
                    searchNewsState.tryEmit(SearchNewsState.SetNews(articleModelListHistory))
                }
            }
        }, catchBlock = { error ->
            searchNewsState.tryEmit(SearchNewsState.EmptyList)
            Log.d(ERROR_DB, error.localizedMessage)
        }, finallyBlock = {
            searchNewsState.tryEmit(SearchNewsState.HideProgress)
        })
    }

    private fun checkUpdateList(newList: List<ArticleUiModel>, action: () -> Unit) {
        if (articleModelListHistory != newList) {
            action()
        }
    }

    private fun saveArticle(articleModel: ArticleUiModel) {
        if (saveHistory) {
            val checkSaved = !articleModel.isFavorites && !articleModel.isHistory
            if (checkSaved) {
                articleModel.isHistory = true
                articleModel.dateAdded = Date().toStringFormatDateDefault()
                viewModelScope.launchJob(tryBlock = {
                    searchNewsInteractor.insertArticle(articleModel.toArticleModel(), accountId)
                    articleModelListHistory.find { articleHistory -> articleHistory.title == articleModel.title }
                        ?.isHistory = true
                    searchNewsState.tryEmit(SearchNewsState.ChangeNews(articleModelListHistory))
                }, catchBlock = { error ->
                    Log.d(ERROR_DB, error.localizedMessage)
                })
            }
        }
    }

    private fun deleteFavorites(articleModel: ArticleUiModel) {
        articleModel.isFavorites = false
        viewModelScope.launchJob(tryBlock = {

            searchNewsInteractor.deleteArticleByIdFavorites(
                articleModel.title,
                accountId
            )

            articleModelListHistory.find { articleHistory -> articleHistory.title == articleModel.title }
                ?.isFavorites = false

            searchNewsState.tryEmit(SearchNewsState.ChangeNews(articleModelListHistory))
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun saveArticleLike(articleModel: ArticleUiModel) {
        articleModel.isFavorites = true
        viewModelScope.launchJob(tryBlock = {
            searchNewsInteractor.insertArticle(articleModel.toArticleModel(), accountId)
            articleModelListHistory.find { articleHistory -> articleHistory.title == articleModel.title }
                ?.isFavorites = true
            searchNewsState.tryEmit(SearchNewsState.ChangeNews(articleModelListHistory))

        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun getSourcesLike() {
        viewModelScope.launchJob(tryBlock = {
            likeSources = searchNewsInteractor.getLikeSources(accountId).toMutableList()
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    private fun getSources() {
        viewModelScope.launchJob(tryBlock = {
            allSources = searchNewsInteractor.getSources()
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    sealed class SearchNewsState {
        data class SetNews(val list: List<ArticleUiModel>) : SearchNewsState()
        data class ChangeNews(val articleModelListHistory: List<ArticleUiModel>) : SearchNewsState()
        data class SetTitle(val historySelectModel: HistorySelectModel) : SearchNewsState()
        object EmptyList : SearchNewsState()
        object HideFavorites : SearchNewsState()
        object HideProgress : SearchNewsState()
        object AddBadge : SearchNewsState()
        object RemoveBadge : SearchNewsState()
        object SheetExpanded : SearchNewsState()
        object StartedState : SearchNewsState()
    }
}
