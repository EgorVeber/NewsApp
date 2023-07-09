package ru.gb.veber.newsapi.presentation.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.domain.interactor.FavoritesInteractor
import ru.gb.veber.newsapi.presentation.base.NewsViewModel
import ru.gb.veber.newsapi.presentation.favorites.viewpager.FavoritesViewPagerAdapter
import ru.gb.veber.newsapi.presentation.mapper.toArticleUiModel
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.viewholder.BaseViewHolder
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import ru.gb.veber.ui_common.TAG_DB_ERROR
import ru.gb.veber.ui_common.coroutine.launchJob
import ru.gb.veber.ui_common.utils.DateFormatter.toFormatDateDefault
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    private val router: Router,
    private val favoritesInteractor: FavoritesInteractor,
) : NewsViewModel() {

    private val _uiState: MutableLiveData<FavoritesState> = MutableLiveData()
    val uiState: LiveData<FavoritesState> = _uiState
    private var accountIdS: Int = 0
    private var listSave: MutableList<ArticleUiModel> = mutableListOf()

    override fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun getAccountArticle(accountID: Int, page: String) {
        accountIdS = accountID
        if (accountID != ACCOUNT_ID_DEFAULT) {
            if (page == FavoritesViewPagerAdapter.FAVORITES) {
                _uiState.value = FavoritesState.Loading
                viewModelScope.launchJob(tryBlock = {
                    val listArticle = favoritesInteractor.getLikeArticle(accountID)
                    if (listArticle.isEmpty()) {
                        _uiState.postValue(FavoritesState.EmptyList)
                    } else {
                        //TODO сделать нормально
                        val changedUiArticleList =
                            listArticle.reversed().map { article ->
                                article.publishedAt = article.publishedAt.toFormatDateDefault()
                                article.toArticleUiModel(viewType = BaseViewHolder.VIEW_TYPE_FAVORITES_NEWS)
                            }
                        _uiState.postValue(FavoritesState.SetSources(changedUiArticleList))
                    }
                },
                    catchBlock = { error ->
                        error.localizedMessage?.let { Log.d(TAG_DB_ERROR, it) }
                    }
                )
            } else {
                _uiState.value = FavoritesState.Loading
                viewModelScope.launchJob(tryBlock = {
                    val listArticle = favoritesInteractor.getHistoryArticle(accountID)
                    if (listArticle.isEmpty()) {
                        _uiState.postValue(FavoritesState.EmptyList)
                    } else {
                        listSave = favoritesInteractor.getListArticleGroupByDate(listArticle)
                            .toMutableList()
                        listSave.map { articleUiModel ->
                            if (articleUiModel.title == SHOW_HISTORY) {
                                articleUiModel.viewType = BaseViewHolder.VIEW_TYPE_FAVORITES_NEWS
                            }
                        }
                        _uiState.postValue(FavoritesState.SetSources(listSave))
                    }
                }, catchBlock = { error ->
                    error.localizedMessage?.let {
                        Log.d(TAG_DB_ERROR, it)
                    }
                })
            }
        } else {
            _uiState.value = FavoritesState.NotAuthorized
        }
    }

    fun clickNews(articleUiModel: ArticleUiModel) {
        _uiState.value = FavoritesState.ClickNews(articleUiModel)
    }

    fun deleteFavorites(articleUiModel: ArticleUiModel) {
        articleUiModel.title?.let { title ->
            viewModelScope.launchJob(tryBlock = {
                favoritesInteractor.deleteArticleFavorites(title, accountIdS)
                val art = (favoritesInteractor.getLikeArticle(accountIdS))
                val artModifiedList = art.reversed().map { article ->
                    article.publishedAt = article.publishedAt.toFormatDateDefault()
                    article.toArticleUiModel(viewType = BaseViewHolder.VIEW_TYPE_FAVORITES_NEWS)
                }
                _uiState.postValue(
                    FavoritesState.SetSources(artModifiedList)
                )
            }, catchBlock = { error ->
                error.localizedMessage?.let { Log.d(TAG_DB_ERROR, it) }
            })
        }
    }

    fun openScreenWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
    }

    fun deleteHistory(articleUiModel: ArticleUiModel) {
        articleUiModel.title?.let { title ->
            viewModelScope.launchJob(tryBlock = {
                favoritesInteractor.deleteArticleHistory(title, accountIdS)
                val art = favoritesInteractor.getHistoryArticle(accountIdS)
                listSave = favoritesInteractor.getListArticleGroupByDate(art).toMutableList()
                listSave.map { articleUiModel ->
                    if (articleUiModel.title == SHOW_HISTORY) {
                        articleUiModel.viewType = BaseViewHolder.VIEW_TYPE_FAVORITES_NEWS
                    }
                }
                _uiState.postValue(FavoritesState.SetSources(listSave))
            }, catchBlock = { error ->
                error.localizedMessage?.let { Log.d(TAG_DB_ERROR, it) }
            })
        }
    }

    fun clickGroupHistory(articleUiModel: ArticleUiModel) {
        listSave.forEach {
            Log.d("clickGroupHistory", it.toString())
        }
        Log.d(
            "clickGroupHistory",
            "clickGroupHistory() called with: articleUiModel = $articleUiModel"
        )

        if (articleUiModel.title == SHOW_HISTORY) {
            listSave.forEach {
                if (it.dateAdded == articleUiModel.publishedAt) {
                    it.showHistory = false
                }
            }
            articleUiModel.title = HIDE_HISTORY
            _uiState.value = FavoritesState.SetSources(listSave.filter { it.showHistory })
        } else {
            listSave.forEach {
                if (it.dateAdded == articleUiModel.publishedAt) {
                    it.showHistory = true
                }
            }
            articleUiModel.title = SHOW_HISTORY
            _uiState.value = FavoritesState.SetSources(listSave.filter { it.showHistory })
        }
    }

    fun deleteGroupHistory(articleUiModel: ArticleUiModel) {
        viewModelScope.launchJob(tryBlock = {
            favoritesInteractor.deleteArticleHistoryGroup(accountIdS, articleUiModel.publishedAt)
            val list = favoritesInteractor.getHistoryArticle(accountIdS)
            listSave = favoritesInteractor.getListArticleGroupByDate(list).toMutableList()
            listSave.map { articleUiModel ->
                if (articleUiModel.title == SHOW_HISTORY) {
                    articleUiModel.viewType = BaseViewHolder.VIEW_TYPE_FAVORITES_NEWS
                }
            }
            _uiState.postValue(FavoritesState.SetSources(listSave))
        }, catchBlock = { error ->
            error.localizedMessage?.let {
                _uiState.postValue(FavoritesState.ErrorDeleteGroup)
                Log.d(TAG_DB_ERROR, it)
            }
        })
    }

    sealed class FavoritesState {
        data class ClickNews(val articleModel: ArticleUiModel) : FavoritesState()
        data class SetSources(val articleModels: List<ArticleUiModel>) : FavoritesState()
        object EmptyList : FavoritesState()
        object Loading : FavoritesState()
        object NotAuthorized : FavoritesState()
        object ErrorDeleteGroup : FavoritesState()
    }

    companion object {
        //TODO  две константы подумать куда вынести
        private const val SHOW_HISTORY = "SHOW_HISTORY"
        private const val HIDE_HISTORY = "HIDE_HISTORY"
    }
}
