package ru.gb.veber.newsapi.view.sources

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.core.SearchNewsScreen
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.HistorySelect
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.database.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepo
import ru.gb.veber.newsapi.model.repository.room.ArticleRepo
import ru.gb.veber.newsapi.model.repository.room.HistorySelectRepo
import ru.gb.veber.newsapi.model.repository.room.SourcesRepo
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.ERROR_DB
import ru.gb.veber.newsapi.utils.mapper.toHistorySelectDbEntity
import javax.inject.Inject

class SourcesViewModel @Inject constructor(
    private val accountSourcesRepoImpl: AccountSourcesRepo,
    private val router: Router,
    private val sourcesRepoImpl: SourcesRepo,
    private val articleRepoImpl: ArticleRepo,
    private val historySelectRepoImpl: HistorySelectRepo,
) : ViewModel() {

    private val mutableFlow: MutableLiveData<SourcesState> = MutableLiveData()
    private val flow: LiveData<SourcesState> = mutableFlow

    private var allSources: MutableList<Sources> = mutableListOf()
    private var likeSources: List<Sources> = mutableListOf()
    private var accountId: Int = 0

    fun subscribe(accountId: Int): LiveData<SourcesState> {
        this.accountId = accountId
        getSources(accountId)
        return flow
    }

    fun getSources(accountId: Int) {
        if (accountId == ACCOUNT_ID_DEFAULT) {
            sourcesRepoImpl.getSources().subscribe({ listSources ->
                allSources = listSources
                mutableFlow.value = SourcesState.SetSources(listSources)
            }, { error ->
                Log.d(ERROR_DB, error.localizedMessage)
            })
        } else {
            Single.zip(
                sourcesRepoImpl.getSources(),
                accountSourcesRepoImpl.getLikeSourcesFromAccount(accountId = accountId),
                articleRepoImpl.getArticleById(accountId = accountId)
            ) { all, like, article ->
                allSources = all
                like.map { sources ->
                    sources.liked = true
                }
                likeSources = like

                for (j in like.size - 1 downTo 0) {
                    for (i in all.indices) {
                        if (like[j].idSources == all[i].idSources) {
                            all.removeAt(i)
                            all.add(0, like[j].also { sources -> sources.liked = true })
                        }
                    }
                }

                all.forEach { sor ->
                    article.forEach { art ->
                        if (sor.idSources == art.sourceId) {
                            if (art.isFavorites) sor.totalFavorites += 1
                            else sor.totalHistory += 1
                        }
                    }
                }
                all
            }.subscribe({ listSources ->
                mutableFlow.value = SourcesState.SetSources(listSources)
            }, { error ->
                Log.d(ERROR_DB, error.localizedMessage)
            })
        }
    }

    fun openWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
    }

    fun imageClick(source: Sources) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            if (source.liked) {
                source.liked = false
                accountSourcesRepoImpl.deleteSourcesLike(accountId = accountId,
                    sourcesId = source.id).subscribe({
                    getSources(accountId)
                }, { error ->
                    Log.d(ERROR_DB, error.localizedMessage)
                })
            } else {
                source.liked = true
                accountSourcesRepoImpl.insert(accountSourcesDbEntity = AccountSourcesDbEntity(
                    accountId, source.id)
                ).subscribe({
                    getSources(accountId)
                }, { error ->
                    Log.d(ERROR_DB, error.localizedMessage)
                })
            }
        } else {
            mutableFlow.value = SourcesState.ShowToastLogIn
        }
    }

    fun openAllNews(source: String?, name: String?) {
        val history =
            HistorySelect(0, accountID = accountId, sourcesId = source, sourcesName = name)
        router.navigateTo(SearchNewsScreen(accountId = accountId, historySelect = history))
        historySelectRepoImpl.insertSelect(historyDbEntity = history.toHistorySelectDbEntity())
            .subscribe({}, { error ->
                Log.d(ERROR_DB, error.localizedMessage)
            })
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    sealed class SourcesState {
        data class SetSources(val list: List<Sources>) : SourcesState()
        object ShowToastLogIn : SourcesState()
    }
}