package ru.gb.veber.newsapi.presentation.sources

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.asSharedFlow
import ru.gb.veber.newsapi.core.SearchNewsScreen
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.domain.interactor.SourceInteractor
import ru.gb.veber.newsapi.domain.models.AccountSourcesModel
import ru.gb.veber.newsapi.domain.models.HistorySelectModel
import ru.gb.veber.newsapi.domain.models.SourcesModel
import ru.gb.veber.newsapi.presentation.base.NewsViewModel
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import ru.gb.veber.ui_common.TAG_DB_ERROR
import ru.gb.veber.ui_common.coroutine.SingleSharedFlow
import ru.gb.veber.ui_common.coroutine.launchJob
import javax.inject.Inject

class SourcesViewModel @Inject constructor(
    private val router: Router,
    private val sourceInteractor: SourceInteractor,
) : NewsViewModel() {

    private val mutableFlow: MutableLiveData<SourcesState> = MutableLiveData()
    private val flow: LiveData<SourcesState> = mutableFlow

    private val showMessageState = SingleSharedFlow<Boolean>()
    val showMessageFlow = showMessageState.asSharedFlow()

    private var allSources: MutableList<SourcesModel> = mutableListOf()
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
                val listSources = sourceInteractor.getSources()
                allSources = listSources
                sendSources(listSources)
            }, catchBlock = { error ->
                Log.d(TAG_DB_ERROR, error.localizedMessage)
            })
        } else {
            viewModelScope.launchJob(tryBlock = {
                val all = sourceInteractor.getSources()
                val like = sourceInteractor.getLikeSourcesFromAccount(accountId = accountId)
                val article = sourceInteractor.getArticleById(accountId = accountId)
                allSources = all

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
                        if (sor.idSources == art.sourceModel.id) {
                            if (art.isFavorites) sor.totalFavorites += 1
                            else sor.totalHistory += 1
                        }
                    }
                }
                sendSources(all)
            }, catchBlock = { error ->
                Log.d(TAG_DB_ERROR, error.localizedMessage)
            })
        }
    }

    fun openWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
    }

    fun imageClick(source: SourcesModel) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            if (source.liked) {
                source.liked = false

                viewModelScope.launchJob(tryBlock = {
                    sourceInteractor.deleteSourcesLike(
                        accountId = accountId,
                        sourcesId = source.id
                    )
                    getSources(accountId)
                }, catchBlock = { error ->
                    Log.d(TAG_DB_ERROR, error.localizedMessage)
                })
            } else {
                source.liked = true
                viewModelScope.launchJob(tryBlock = {
                    sourceInteractor.insert(
                        accountSourcesModel = AccountSourcesModel(
                            accountId,
                            source.id
                        )
                    )
                    getSources(accountId)
                }, catchBlock = { error ->
                    Log.d(TAG_DB_ERROR, error.localizedMessage)
                })
            }

        } else {
            showMessageState.tryEmit(true)
        }
    }

    fun openAllNews(source: String?, name: String?) {
        val historySelect = HistorySelectModel(0, accountID = accountId, sourcesId = source, sourcesName = name)
        router.navigateTo(SearchNewsScreen(accountId = accountId, historySelectModel = historySelect))
        viewModelScope.launchJob(tryBlock = {
            sourceInteractor.insertSelect(historySelect)
        }, catchBlock = { error ->
            Log.d(TAG_DB_ERROR, error.localizedMessage)
        })
    }

    fun setFilter(filter: String) {
        sourceFilter = if (spaceTest(filter)) "" else filter
        sendSources(allSources)
    }

    private fun spaceTest(text: String): Boolean {
        val result = text.trim()
        return result.isEmpty()
    }

    private fun filtered(list: List<SourcesModel>): List<SourcesModel> {
        return if (sourceFilter == "") list
        else {
            val foundByNames = (list.filter { sources ->
                sources.name?.contains(sourceFilter, ignoreCase = true) ?: false
            })
            val foundByCountries = (list.filter { sources ->
                sources.country?.contains(sourceFilter, ignoreCase = true) ?: false
            })
            val result = (foundByNames + foundByCountries).toSet().toList()
            result
        }
    }

    fun focusOne(source: SourcesModel, type: Int) {
        focusedSources[source.idSources!!] = type
    }

    fun focusAll(type: Int) {
        allSources.forEach { sources ->
            focusOne(sources, type)
        }
        sendSources(allSources)
    }

    private fun sendSources(list: List<SourcesModel>) {
        val result = list.map { sources ->
            if (focusedSources[sources.idSources] != null) sources.copy(focusType = focusedSources[sources.idSources]!!) else sources
        }
        mutableFlow.postValue(SourcesState.SetSources(filtered(result)))
    }

    override fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    sealed class SourcesState {
        data class SetSources(val list: List<SourcesModel>) : SourcesState()
    }
}
