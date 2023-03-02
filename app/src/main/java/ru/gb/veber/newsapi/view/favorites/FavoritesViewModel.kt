package ru.gb.veber.newsapi.view.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.repository.room.ArticleRepo
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.ERROR_DB
import ru.gb.veber.newsapi.utils.extentions.formatDateTime
import ru.gb.veber.newsapi.utils.extentions.launchJob
import ru.gb.veber.newsapi.utils.mapper.HIDE_HISTORY
import ru.gb.veber.newsapi.utils.mapper.SHOW_HISTORY
import ru.gb.veber.newsapi.utils.mapper.toArticle
import ru.gb.veber.newsapi.utils.mapper.toNewListArticleGroupByDate
import ru.gb.veber.newsapi.utils.extentions.stringFromData
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerAdapter
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.viewholder.BaseViewHolder
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    private val router: Router,
    private val articleRepoImpl: ArticleRepo,
) : ViewModel() {

    private val _uiState: MutableLiveData<FavoritesState> = MutableLiveData()
    val uiState: LiveData<FavoritesState> = _uiState
    private var accountIdS: Int = 0
    private var listSave: MutableList<Article> = mutableListOf()

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun getAccountArticle(accountID: Int, page: String) {
        accountIdS = accountID
        if (accountID != ACCOUNT_ID_DEFAULT) {
            if (page == FavoritesViewPagerAdapter.FAVORITES) {
                _uiState.value = FavoritesState.Loading
                viewModelScope.launchJob(tryBlock = {
                    val listArticleDb = articleRepoImpl.getLikeArticleByIdV2(accountID)
                    if (listArticleDb.isEmpty()) {
                        _uiState.postValue(FavoritesState.EmptyList)
                    } else {
                        _uiState.postValue(FavoritesState.SetSources(listArticleDb.map { articleDb ->
                            articleDb.toArticle()
                        }.reversed().map { article ->
                            article.publishedAtChange =
                                stringFromData(article.publishedAt).formatDateTime()
                            article.viewType = BaseViewHolder.VIEW_TYPE_FAVORITES_NEWS
                            article
                        }))
                    }
                },
                    catchBlock = { error ->
                        error.localizedMessage?.let { Log.d(ERROR_DB, it) }
                    }
                )
            } else {
                _uiState.value = FavoritesState.Loading
                viewModelScope.launchJob(tryBlock = {
                    val listArticle = articleRepoImpl.getHistoryArticleByIdV2(accountID)
                    if (listArticle.isEmpty()) {
                        _uiState.postValue(FavoritesState.EmptyList)
                    } else {
                        listSave = listArticle.map { articleDb ->
                            articleDb.toArticle()
                        }.toNewListArticleGroupByDate()
                        _uiState.postValue(FavoritesState.SetSources(listSave))
                    }
                }, catchBlock = { error ->
                    error.localizedMessage?.let { Log.d(ERROR_DB, it) }
                })
            }
        } else {
            _uiState.value = FavoritesState.NotAuthorized
        }
    }

    fun clickNews(article: Article) {
        _uiState.value = FavoritesState.ClickNews(article)
    }

    fun deleteFavorites(article: Article) {
        article.title?.let { title ->
            viewModelScope.launchJob(tryBlock = {
                articleRepoImpl.deleteArticleByIdFavoritesV2(title, accountIdS)
                val art = (articleRepoImpl.getLikeArticleByIdV2(accountIdS))
                _uiState.postValue(
                    FavoritesState.SetSources(art.map { articleDb ->
                        articleDb.toArticle()
                    }.reversed()
                        .map { art ->
                            art.publishedAtChange = stringFromData(art.publishedAt).formatDateTime()
                            art.viewType = BaseViewHolder.VIEW_TYPE_FAVORITES_NEWS
                            art
                        })
                )
            }, catchBlock = { error ->
                error.localizedMessage?.let { Log.d(ERROR_DB, it) }
            })
        }
    }

    fun openScreenWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
    }

    fun deleteHistory(article: Article) {
        article.title?.let { title ->
            viewModelScope.launchJob(tryBlock = {
                articleRepoImpl.deleteArticleByIdHistoryV2(title, accountIdS)
                val art = (articleRepoImpl.getHistoryArticleByIdV2(accountIdS))
                listSave = art.map { articleDb ->
                    articleDb.toArticle()
                }.toNewListArticleGroupByDate()
                _uiState.postValue(FavoritesState.SetSources(listSave))
            }, catchBlock = { error ->
                error.localizedMessage?.let { Log.d(ERROR_DB, it) }
            })
        }
    }

    fun clickGroupHistory(article: Article) {
        if (article.title == SHOW_HISTORY) {
            listSave.forEach {
                if (it.dateAdded == article.publishedAt) {
                    it.showHistory = false
                }
            }
            article.title = HIDE_HISTORY
            _uiState.value = FavoritesState.SetSources(listSave.filter { it.showHistory })
        } else {
            listSave.forEach {
                if (it.dateAdded == article.publishedAt) {
                    it.showHistory = true
                }
            }
            article.title = SHOW_HISTORY
            _uiState.value = FavoritesState.SetSources(listSave.filter { it.showHistory })
        }
    }

    fun deleteGroupHistory(article: Article) {
        viewModelScope.launchJob(tryBlock = {
            articleRepoImpl.deleteArticleByIdHistoryGroupV2(accountIdS, article.publishedAt)
            viewModelScope.launchJob(tryBlock = {
                val list = articleRepoImpl.getHistoryArticleByIdV2(accountIdS)
                listSave = list.map { articleDb ->
                    articleDb.toArticle()
                }.toNewListArticleGroupByDate()
                _uiState.postValue(FavoritesState.SetSources(listSave))
            }, catchBlock = {
                    error ->
                error.localizedMessage?.let { Log.d(ERROR_DB, it) }
            })
        }, catchBlock = { error ->
            error.localizedMessage?.let { Log.d(ERROR_DB, it) }
        })
    }

    sealed class FavoritesState {
        data class ClickNews(val article: Article) : FavoritesState()
        data class SetSources(val articles: List<Article>) : FavoritesState()
        object EmptyList : FavoritesState()
        object Loading : FavoritesState()
        object NotAuthorized : FavoritesState()
    }
}
