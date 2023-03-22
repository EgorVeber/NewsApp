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
    private var focusedSources: MutableMap<String, Int> = mutableMapOf()
    private var sourceFilter = ""
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
                sendSources(listSources)
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
                sendSources(all)
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
        val history = HistorySelect(0, accountID = accountId, sourcesId = source, sourcesName = name)
        router.navigateTo(SearchNewsScreen(accountId = accountId, historySelect = history))
        viewModelScope.launchJob(tryBlock = {
            sourceInteractor.insertSelectV2(historyDbEntity = history.toHistorySelectDbEntity())
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }
    fun setFilter(filter: String) {
        sourceFilter = if (spaceTest(filter)) ""
        else filter
        sendSources(allSources)
    }

    private fun spaceTest(text: String): Boolean {
        val result = text.trim()
        return result.isEmpty()
    }

    private fun filtered(list: List<Sources>):  List<Sources>{
        return if (sourceFilter == "") list
        else ((list.filter { it.name?.contains(sourceFilter, ignoreCase = true) ?: false }) +
                (list.filter { it.country?.contains(sourceFilter, ignoreCase = true) ?: false })).toSet().toList()
    }

    fun focusOne(source: Sources, type: Int) {
        focusedSources[source.idSources!!]=type
    }

    fun focusAll(type: Int) {
        allSources.forEach { sources ->
            focusOne(sources,type)
        }
        sendSources(allSources)
    }

    private fun sendSources(list: List<Sources>) {
        val result = list.map { if (focusedSources[it.idSources] != null) it.copy(focusType = focusedSources[it.idSources]!!) else it }
        mutableFlow.postValue(SourcesState.SetSources(filtered(result)))
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
