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

    fun getAccountArticle(accountID: Int, isFavoritesPage: Boolean) {
        _uiState.value = FavoritesState.Loading
        accountIdS = accountID
        if (accountID != ACCOUNT_ID_DEFAULT) {
            if (isFavoritesPage) getFavoritesArticle(accountID)
            else getHistoryArticle(accountID)
        } else {
            _uiState.value = FavoritesState.NotAuthorized
        }
    }

    private fun getFavoritesArticle(accountID: Int) {
        viewModelScope.launchJob(
            tryBlock = {
                val listArticle = favoritesInteractor.getLikeArticle(accountID)
                if (listArticle.isEmpty()) {
                    _uiState.postValue(FavoritesState.EmptyList)
                    return@launchJob
                }
                listArticle.map { article ->
                    article.toArticleUiModel(
                        noChangeDateFormat = true,
                        viewType = BaseViewHolder.VIEW_TYPE_FAVORITES_NEWS
                    )
                }.run {
                    _uiState.postValue(FavoritesState.SetSources(this))
                }
            },
            catchBlock = { error ->
                error.localizedMessage?.let { Log.d(TAG_DB_ERROR, it) }
            }
        )
    }

    private fun getHistoryArticle(accountID: Int) {
        viewModelScope.launchJob(tryBlock = {
            val listGrouping =
                favoritesInteractor.getListArticlesGroupByDate(accountID, SHOW_HISTORY)
                    .map { article ->
                        article.toArticleUiModel(
                            true,
                            BaseViewHolder.VIEW_TYPE_HISTORY_NEWS
                        )
                    }
            if (listGrouping.isEmpty()) {
                _uiState.postValue(FavoritesState.EmptyList)
                return@launchJob
            }

            listSave = listGrouping.map { articleUiModel ->
                if (articleUiModel.title == SHOW_HISTORY) {
                    articleUiModel.viewType = BaseViewHolder.VIEW_TYPE_HISTORY_HEADER
                }
                articleUiModel
            }.toMutableList()


            _uiState.postValue(FavoritesState.SetSources(listSave))

        }, catchBlock = { error ->
            error.localizedMessage?.let {
                Log.d(TAG_DB_ERROR, it)
            }
        })
    }

    fun deleteFavorites(articleUiModel: ArticleUiModel) {
        viewModelScope.launchJob(tryBlock = {
            favoritesInteractor.deleteArticleFavorites(articleUiModel.title, accountIdS)
            getFavoritesArticle(accountIdS)
        }, catchBlock = { error ->
            error.localizedMessage?.let { Log.d(TAG_DB_ERROR, it) }
        })
    }

    fun deleteHistory(articleUiModel: ArticleUiModel) {
        viewModelScope.launchJob(tryBlock = {
            favoritesInteractor.deleteArticleHistory(articleUiModel.title, accountIdS)
            getHistoryArticle(accountIdS)
        }, catchBlock = { error ->
            error.localizedMessage?.let { Log.d(TAG_DB_ERROR, it) }
        })
    }

    fun clickGroupHistory(articleUiModel: ArticleUiModel) {
        listSave.forEach { article ->
            if (article.dateAdded == articleUiModel.dateAdded) {
                article.showHistory = articleUiModel.title != SHOW_HISTORY
            }
        }

        if (articleUiModel.title == SHOW_HISTORY) {
            articleUiModel.title = HIDE_HISTORY
            articleUiModel.showHistory = true
        } else {
            articleUiModel.title = SHOW_HISTORY
        }

        val filteredList = listSave.filter { article -> article.showHistory }
        _uiState.value = FavoritesState.SetSources(filteredList)
    }

    fun deleteGroupHistory(articleUiModel: ArticleUiModel) {
        viewModelScope.launchJob(tryBlock = {
            favoritesInteractor.deleteArticleHistoryGroup(accountIdS, articleUiModel.dateAdded)
            getHistoryArticle(accountIdS)
            _uiState.postValue(FavoritesState.SetSources(listSave))
        }, catchBlock = { error ->
            error.localizedMessage?.let {
                _uiState.postValue(FavoritesState.ErrorDeleteGroup)
                Log.d(TAG_DB_ERROR, it)
            }
        })
    }

    fun clickNews(articleUiModel: ArticleUiModel) {
        _uiState.value = FavoritesState.ClickNews(articleUiModel)
    }

    fun openScreenWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
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
        private const val SHOW_HISTORY = "SHOW_HISTORY"
        private const val HIDE_HISTORY = "HIDE_HISTORY"
    }
}
