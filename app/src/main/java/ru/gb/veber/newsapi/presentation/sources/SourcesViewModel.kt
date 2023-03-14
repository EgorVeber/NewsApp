package ru.gb.veber.newsapi.presentation.sources

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.common.base.NewsViewModel
import ru.gb.veber.newsapi.common.extentions.launchJob
import ru.gb.veber.newsapi.common.screen.SearchNewsScreen
import ru.gb.veber.newsapi.common.screen.WebViewScreen
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.ERROR_DB
import ru.gb.veber.newsapi.data.mapper.toHistorySelectDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.domain.interactor.SourceInteractor
import ru.gb.veber.newsapi.domain.models.HistorySelect
import ru.gb.veber.newsapi.domain.models.Sources
import javax.inject.Inject

class SourcesViewModel @Inject constructor(
    private val router: Router,
    private val sourceInteractor: SourceInteractor,
) : NewsViewModel() {

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
            viewModelScope.launchJob(tryBlock = {
                val listSources = sourceInteractor.getSourcesV2()
                allSources = listSources
                mutableFlow.postValue(SourcesState.SetSources(listSources))
            }, catchBlock = { error ->
                Log.d(ERROR_DB, error.localizedMessage)
            })
        } else {
            viewModelScope.launchJob(tryBlock = {
                val all = sourceInteractor.getSourcesV2()
                val like = sourceInteractor.getLikeSourcesFromAccountV2(accountId = accountId)
                val article = sourceInteractor.getArticleByIdV2(accountId = accountId)
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

                mutableFlow.postValue(SourcesState.SetSources(all))

            }, catchBlock = { error ->
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

                viewModelScope.launchJob(tryBlock = {
                    sourceInteractor.deleteSourcesLikeV2(
                        accountId = accountId,
                        sourcesId = source.id
                    )
                    getSources(accountId)
                }, catchBlock = { error ->
                    Log.d(ERROR_DB, error.localizedMessage)
                })
            } else {
                source.liked = true
                viewModelScope.launchJob(tryBlock = {
                    sourceInteractor.insertV2(
                        accountSourcesDbEntity = AccountSourcesDbEntity(
                            accountId, source.id
                        )
                    )
                    getSources(accountId)
                }, catchBlock = { error ->
                    Log.d(ERROR_DB, error.localizedMessage)
                })
            }

        } else {
            mutableFlow.postValue(SourcesState.ShowToastLogIn)
        }
    }

    fun openAllNews(source: String?, name: String?) {
        val history =
            HistorySelect(0, accountID = accountId, sourcesId = source, sourcesName = name)
        router.navigateTo(SearchNewsScreen(accountId = accountId, historySelect = history))
        viewModelScope.launchJob(tryBlock = {
            sourceInteractor.insertSelectV2(historyDbEntity = history.toHistorySelectDbEntity())
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    override fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    sealed class SourcesState {
        data class SetSources(val list: List<Sources>) : SourcesState()
        object ShowToastLogIn : SourcesState()
    }
}
