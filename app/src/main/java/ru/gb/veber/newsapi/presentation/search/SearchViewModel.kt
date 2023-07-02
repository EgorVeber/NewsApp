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
import ru.gb.veber.newsapi.common.extentions.DateFormatter
import ru.gb.veber.newsapi.common.extentions.DateFormatter.toDateFormatDateSeparator
import ru.gb.veber.newsapi.common.extentions.DateFormatter.toStringFormatDateSeparator
import ru.gb.veber.newsapi.common.extentions.DateFormatter.toStringFormatDateYearMonthDay
import ru.gb.veber.newsapi.common.extentions.launchJob
import ru.gb.veber.newsapi.common.screen.SearchNewsScreen
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.ERROR_DB
import ru.gb.veber.newsapi.common.utils.NOT_INPUT_DATE
import ru.gb.veber.newsapi.domain.interactor.SearchInteractor
import ru.gb.veber.newsapi.domain.models.HistorySelectModel
import ru.gb.veber.newsapi.domain.models.SourcesModel
import java.util.Date
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val router: Router,
    private val searchInteractor: SearchInteractor,
) : NewsViewModel() {

    private lateinit var allSources: List<SourcesModel>

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

                // TODO NOT_INPUT_DATE убрать пекредалть на empty
                val historySelect = createHistorySelect(
                    sourcesId = sourcesId,
                    sortBySources = sortBy,
                    sourcesName = sourcesName,
                    dateSources = if (date != NOT_INPUT_DATE) date.toDateFormatDateSeparator()
                        .toStringFormatDateYearMonthDay() else ""
                )

                router.navigateTo(SearchNewsScreen(accountId, historySelect))
                saveHistorySelect(historySelect)

            } else searchViewState.tryEmit(SearchState.ErrorDateInput)
        } else searchViewState.tryEmit(SearchState.SelectSources)
    }

    fun onClickSearch(keyWord: String, searchIn: String, sortBy: String, sourcesName: String) {
        if (keyWord.isNotEmpty()) {
            val historySelectModel: HistorySelectModel
            if (sourcesName.isNotEmpty()) {
                if (containsByName(sourcesName)) {
                    val sourcesId = getIdByNameIfEmpty(sourcesName)
                    historySelectModel = createHistorySelect(
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
                historySelectModel = createHistorySelect(
                    keyWord = keyWord,
                    searchIn = searchIn,
                    sortByKeyWord = sortBy
                )
            }
            router.navigateTo(SearchNewsScreen(accountId, historySelectModel))
            saveHistorySelect(historySelectModel)
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

    fun onClickHistoryIconDelete(historySelectModel: HistorySelectModel) {
        viewModelScope.launchJob(tryBlock = {
            searchInteractor.deleteSelect(historySelectModel)
            getHistorySelect()
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.toString())
        })
    }

    fun onClickHistoryItem(historySelectModel: HistorySelectModel) {
        router.navigateTo(SearchNewsScreen(accountId, historySelectModel))
    }

    fun pikerPositive(timeMillis: Long) {
        searchViewState.tryEmit(SearchState.SetDay(timeMillis.toStringFormatDateSeparator()))
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

    private fun saveHistorySelect(historySelectModel: HistorySelectModel) {
        if (saveHistory) {
            viewModelScope.launchJob(tryBlock = {
                searchInteractor.insertSelect(historySelectModel)
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
    ) = HistorySelectModel(
        id = 0,
        accountID = accountId,
        keyWord = keyWord,
        searchIn = searchIn,
        sortByKeyWord = sortByKeyWord,
        sortBySources = sortBySources,
        sourcesId = sourcesId,
        dateSources = dateSources,
        sourcesName = sourcesName
    )

    private fun getIdByNameIfEmpty(sourcesName: String): String {
        return allSources.find { sources ->
            sources.name == sourcesName
        }?.idSources ?: allSources[0].idSources ?: ""
    }

    private fun containsByName(sourcesName: String): Boolean {
        return allSources.map { it.name }.contains(sourcesName)
    }


    //TODO Сделать по проше
    private fun String.isValidatePeriod(): Boolean {
        if (this == NOT_INPUT_DATE) return true
        return !(toDateFormatDateSeparator() > Date()
                || toDateFormatDateSeparator() <= DateFormatter.getCurrentDateMinusDay(
            MAX_PERIOD_DAY_NEWS
        ))
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
        data class SetSources(val sources: List<SourcesModel>) : DataState()
        data class SetHistorySelect(val historySelectModel: List<HistorySelectModel>) : DataState()
    }

    companion object {
        const val MAX_PERIOD_DAY_NEWS = -30
    }
}
