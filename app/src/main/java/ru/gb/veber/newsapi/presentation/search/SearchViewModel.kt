package ru.gb.veber.newsapi.presentation.search

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.gb.veber.newsapi.common.base.NewsViewModel
import ru.gb.veber.newsapi.common.extentions.formatDate
import ru.gb.veber.newsapi.common.extentions.isValidatePeriod
import ru.gb.veber.newsapi.common.extentions.launchJob
import ru.gb.veber.newsapi.common.extentions.stringFromDataPiker
import ru.gb.veber.newsapi.common.extentions.toStringDate
import ru.gb.veber.newsapi.common.screen.SearchNewsScreen
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.ERROR_DB
import ru.gb.veber.newsapi.common.utils.NOT_INPUT_DATE
import ru.gb.veber.newsapi.domain.interactor.SearchInteractor
import ru.gb.veber.newsapi.domain.models.HistorySelect
import ru.gb.veber.newsapi.domain.models.Sources
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val router: Router,
    private val searchInteractor: SearchInteractor,
) : NewsViewModel() {

    private lateinit var allSources: List<Sources>

    private val searchViewState = MutableStateFlow<SearchState>(SearchState.Started)
    internal val searchViewFlow = searchViewState.asStateFlow()

    private val historyStateContainer = MutableStateFlow<HistoryState>(HistoryState.Started)
    internal val historyStateFlow = historyStateContainer.asStateFlow()

    private val dataState = MutableSharedFlow<DataState>(replay = 2)
    internal val dataStateFlow = dataState.asSharedFlow()

    private var accountId: Int = ACCOUNT_ID_DEFAULT
    private var saveHistory: Boolean = false
    private var displayOnlySources: Boolean = false

    override fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }


    fun onViewInited(accountId: Int) {
        this.accountId = accountId
        getHistorySelect()
        viewModelScope.launchJob(tryBlock = {
            getAccountInfo(accountId)
            getSources()
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.toString())
        })
    }

    fun changeSearchCriteria(cheked: Boolean) {
        if (!cheked) {
            searchViewState.tryEmit(SearchState.SearchInShow)
        } else {
            searchViewState.tryEmit(SearchState.SourcesInShow)
        }
    }

    fun onClickSearchSources(
        date: String,
        sourcesName: String,
        sortBy: String,
    ) {
        if (containsByName(sourcesName)) {
            if (date.isValidatePeriod()) {
                val sourcesId = getIdByNameIfEmpty(sourcesName)

                val historySelect = createHistorySelect(
                    sourcesId = sourcesId,
                    sortBySources = sortBy,
                    sourcesName = sourcesName,
                    dateSources = if (date != NOT_INPUT_DATE) stringFromDataPiker(date).formatDate() else "")

                router.navigateTo(SearchNewsScreen(accountId, historySelect))
                saveHistorySelect(historySelect)

            } else searchViewState.tryEmit(SearchState.ErrorDateInput)
        } else searchViewState.tryEmit(SearchState.SelectSources)
    }

    fun onClickSearch(keyWord: String, searchIn: String, sortBy: String, sourcesName: String) {
        if (keyWord.isNotEmpty()) {
            val historySelect: HistorySelect
            if (sourcesName.isNotEmpty()) {
                if (containsByName(sourcesName)) {
                    val sourcesId = getIdByNameIfEmpty(sourcesName)
                    historySelect = createHistorySelect(
                        keyWord = keyWord,
                        searchIn = searchIn,
                        sortByKeyWord = sortBy,
                        sourcesId = sourcesId,
                        sourcesName = sourcesName,
                    )
                } else {
                    searchViewState.tryEmit(SearchState.SelectSources)
                    return
                }
            } else {
                historySelect = createHistorySelect(
                    keyWord = keyWord,
                    searchIn = searchIn,
                    sortByKeyWord = sortBy)
            }
            router.navigateTo(SearchNewsScreen(accountId, historySelect))
            saveHistorySelect(historySelect)
        } else {
            searchViewState.tryEmit(SearchState.EnterKeys)
        }
    }

    fun onClickHistoryDelete() {
        viewModelScope.launchJob(tryBlock = {
            searchInteractor.deleteSelectById(accountId)
            dataState.emit(DataState.SetHistorySelect(listOf()))
            historyStateContainer.update { HistoryState.StatusTextHistoryShow }
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.toString())
        })
    }

    fun onClickHistoryIconDelete(historySelect: HistorySelect) {
        viewModelScope.launchJob(tryBlock = {
            searchInteractor.deleteSelect(historySelect)
            getHistorySelect()
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.toString())
        })
    }

    fun onClickHistoryItem(historySelect: HistorySelect) {
        router.navigateTo(SearchNewsScreen(accountId, historySelect))
    }

    fun pikerPositive(timeMillis: Long) {
        searchViewState.tryEmit(SearchState.SetDay(timeMillis.toStringDate()))
    }

    fun pikerNegative() {
        searchViewState.tryEmit(SearchState.PikerNegative)
    }

    private fun getHistorySelect() {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            viewModelScope.launchJob(tryBlock = {
                val historySelectList = searchInteractor.getHistoryById(accountId)

                if (historySelectList.isEmpty()) {
                    historyStateContainer.update { HistoryState.StatusTextHistoryShow }
                } else {
                    historyStateContainer.update { HistoryState.StatusTextHistoryHide }
                }

                dataState.emit(DataState.SetHistorySelect(historySelectList))

            }, catchBlock = { error ->
                Log.d(ERROR_DB, error.toString())
            })
        } else historyStateContainer.value = HistoryState.HideSelectHistory
    }

    private suspend fun getAccountInfo(accountId: Int) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            searchInteractor.getAccountById(accountId).also { account ->
                saveHistory = account.saveSelectHistory
                displayOnlySources = account.displayOnlySources
            }
        }
    }

    private suspend fun getSources() {
        allSources = searchInteractor.getSourcesAccount(accountId, displayOnlySources)
        dataState.emit(DataState.SetSources(allSources))
    }

    private fun saveHistorySelect(historySelect: HistorySelect) {
        if (saveHistory) {
            viewModelScope.launchJob(tryBlock = {
                searchInteractor.insertSelect(historySelect)
            }, catchBlock = { error ->
                Log.d(ERROR_DB, error.toString())
            })
        }
    }

    private fun createHistorySelect(
        keyWord: String? = "",
        searchIn: String? = "",
        sortByKeyWord: String? = "",
        sortBySources: String? = "",
        sourcesId: String? = "",
        dateSources: String? = "",
        sourcesName: String? = "",
    ) = HistorySelect(id = 0,
        accountID = accountId,
        keyWord = keyWord,
        searchIn = searchIn,
        sortByKeyWord = sortByKeyWord,
        sortBySources = sortBySources,
        sourcesId = sourcesId,
        dateSources = dateSources,
        sourcesName = sourcesName)

    private fun getIdByNameIfEmpty(sourcesName: String): String {
        return allSources.find { sources ->
            sources.name == sourcesName
        }?.idSources ?: allSources[0].idSources ?: ""
    }

    private fun containsByName(sourcesName: String): Boolean {
        return allSources.map { it.name }.contains(sourcesName)
    }

    sealed class SearchState {
        data class SetDay(val dateDay: String) : SearchState()
        object SearchInShow : SearchState()
        object PikerNegative : SearchState()
        object SelectSources : SearchState()
        object EnterKeys : SearchState()
        object ErrorDateInput : SearchState()
        object SourcesInShow : SearchState()
        object Started : SearchState()
    }

    sealed class HistoryState {
        object HideSelectHistory : HistoryState()
        object StatusTextHistoryShow : HistoryState()
        object StatusTextHistoryHide : HistoryState()
        object Started : HistoryState()
    }

    sealed class DataState {
        data class SetSources(val sources: List<Sources>) : DataState()
        data class SetHistorySelect(val historySelect: List<HistorySelect>) : DataState()
    }
}
