package ru.gb.veber.newsapi.view.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.core.SearchNewsScreen
import ru.gb.veber.newsapi.model.HistorySelect
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.repository.room.AccountRepo
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepo
import ru.gb.veber.newsapi.model.repository.room.HistorySelectRepo
import ru.gb.veber.newsapi.model.repository.room.SourcesRepo
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.utils.mapper.toHistorySelect
import ru.gb.veber.newsapi.utils.mapper.toHistorySelectDbEntity
import java.util.*
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val router: Router,
    private val accountRepo: AccountRepo,
    private val historySelectRepo: HistorySelectRepo,
    private val sourcesRepo: SourcesRepo,
    private val accountSourcesRepo: AccountSourcesRepo,
) : ViewModel() {

    private lateinit var allSources: MutableList<Sources>
    private lateinit var likeSources: List<Sources>
    private var accountHistorySelect: Boolean = false

    private val mutableFlow: MutableLiveData<SearchState> = MutableLiveData()
    private val flow: LiveData<SearchState> = mutableFlow
    private var accountId: Int = 0

    fun subscribe(accountId: Int): LiveData<SearchState> {
        this.accountId = accountId
        return flow
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun getSources() {
        if (accountId == ACCOUNT_ID_DEFAULT) {
            mutableFlow.value = SearchState.HideSelectHistory
            mutableFlow.value = SearchState.HideEmptyList

            sourcesRepo.getSources().subscribe({
                allSources = it
                mutableFlow.value = SearchState.SetSources(it)
            }, {
                it.localizedMessage?.let { it1 -> Log.d(ERROR_DB, it1) }
            })
        } else {
            Single.zip(
                sourcesRepo.getSources(),
                accountSourcesRepo.getLikeSourcesFromAccount(accountId),
                accountRepo.getAccountById(accountId)
            ) { all, like, account ->

                accountHistorySelect = account.saveSelectHistory
                allSources = all
                like.map { it.isLike = true }
                likeSources = like

                if (account.displayOnlySources && like.isNotEmpty()) {
                    likeSources = like
                    return@zip like
                }
                if (like.isEmpty()) {
                    return@zip all
                } else {
                    for (j in like.size - 1 downTo 0) {
                        for (i in all.indices) {
                            if (like[j].idSources == all[i].idSources) {
                                all.removeAt(i)
                                all.add(0, like[j].also { it.isLike = true })
                            }
                        }
                    }
                    all
                }
            }.subscribe({
                mutableFlow.value = SearchState.SetSources(it)
            }, {
                it.localizedMessage?.let { it1 -> Log.d(ERROR_DB, it1) }
            })
        }
    }

    fun changeSearchCriteria(b: Boolean) {
        if (!b) {
            mutableFlow.value = SearchState.SearchInShow
        } else {
            mutableFlow.value = SearchState.SourcesInShow
        }
    }

    fun openScreenAllNewsSources(
        date: String,
        sourcesName: String,
        sortBy: String,
    ) {
        if (sourcesName.isEmpty() || !allSources.map { it.name }.contains(sourcesName)) {
            mutableFlow.value = SearchState.SelectSources
        } else {
            if (!checkDate(date)) {
                mutableFlow.value = SearchState.ErrorDateInput
            } else {
                val sourcesId = allSources.find { it.name == sourcesName }?.idSources
                if (date == NOT_INPUT_DATE) {
                    val x = HistorySelect(
                        0, accountID = accountId,
                        sourcesId = sourcesId,
                        sortBySources = sortBy,
                        sourcesName = sourcesName,
                    )
                    router.navigateTo(SearchNewsScreen(accountId, x))

                    if (accountHistorySelect) {
                        historySelectRepo.insertSelect(x.toHistorySelectDbEntity())
                            .subscribe({}, {
                                it.localizedMessage?.let { it1 -> Log.d(ERROR_DB, it1) }
                            })
                    }
                } else {
                    val x = HistorySelect(
                        0, accountID = accountId,
                        sourcesId = sourcesId,
                        sortBySources = sortBy,
                        sourcesName = sourcesName,
                        dateSources = stringFromDataPiker(date).formatDate()
                    )

                    router.navigateTo(SearchNewsScreen(accountId, x))
                    if (accountHistorySelect) {
                        historySelectRepo.insertSelect(x.toHistorySelectDbEntity())
                            .subscribe({}, {
                                it.localizedMessage?.let { it1 -> Log.d(ERROR_DB, it1) }
                            })
                    }
                }
            }
        }
    }

    fun openScreenAllNews(
        keyWord: String,
        searchIn: String,
        sortBy: String,
        sourcesName: String,
    ) {
        if (sourcesName.isEmpty()) {
            val x = HistorySelect(
                id = 0,
                accountID = accountId,
                keyWord = keyWord,
                searchIn = searchIn,
                sortByKeyWord = sortBy
            )

            router.navigateTo(SearchNewsScreen(accountId, x))
            if (accountHistorySelect) {
                historySelectRepo.insertSelect(x.toHistorySelectDbEntity()).subscribe({}, {
                    it.localizedMessage?.let { it1 -> Log.d(ERROR_DB, it1) }
                })
            }
        } else {
            if (!allSources.map { it.name }.contains(sourcesName)) {
                mutableFlow.value = SearchState.SelectSources
            } else {
                val sourcesId = allSources.find { it.name == sourcesName }?.idSources

                val x = HistorySelect(
                    id = 0,
                    accountID = accountId,
                    keyWord = keyWord,
                    searchIn = searchIn,
                    sortByKeyWord = sortBy,
                    sourcesId = sourcesId,
                    sourcesName = sourcesName
                )
                router.navigateTo(SearchNewsScreen(accountId, x))

                if (accountHistorySelect) {
                    historySelectRepo.insertSelect(x.toHistorySelectDbEntity())
                        .subscribe({}, {
                            it.localizedMessage?.let { it1 -> Log.d(ERROR_DB, it1) }
                        })
                }
            }
        }
    }

    fun pikerPositive(timeMillis: Long) {
        mutableFlow.value = SearchState.PikerPositive(timeMillis)
    }

    fun pikerNegative() {
        mutableFlow.value = SearchState.PikerNegative
    }

    fun getHistorySelect() {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            historySelectRepo.getHistoryById(accountId)
                .subscribe({ listSelectDbEntity ->
                    if (listSelectDbEntity.isEmpty()) {
                        mutableFlow.value = SearchState.SetHistory(listOf())
                        mutableFlow.value = SearchState.EmptyHistory
                    } else {
                        mutableFlow.value =
                            SearchState.SetHistory(listSelectDbEntity.map { historySelectDbEntity ->
                                historySelectDbEntity.toHistorySelect()
                            }.reversed())
                    }
                }, {
                    it.localizedMessage?.let { it1 -> Log.d(ERROR_DB, it1) }
                })
        }
    }

    fun openScreenNewsHistory(historySelect: HistorySelect) {
        router.navigateTo(SearchNewsScreen(accountId, historySelect))
    }

    fun clearHistory() {
        historySelectRepo.deleteSelectById(accountId).subscribe({
            mutableFlow.value = SearchState.SetHistory(listOf())
            mutableFlow.value = SearchState.EmptyHistory
        }, {
            it.localizedMessage?.let { it1 -> Log.d(ERROR_DB, it1) }
        })
    }

    fun deleteHistory(historySelect: HistorySelect) {
        historySelectRepo.deleteSelect(historySelect.toHistorySelectDbEntity()).subscribe({
            getHistorySelect()
        }, {
            it.localizedMessage?.let { it1 -> Log.d(ERROR_DB, it1) }
        })
    }

    private fun checkDate(date: String): Boolean {
        if (date == NOT_INPUT_DATE) {
            return true
        }
        return !(stringFromDataNews(date) > Date() || stringFromDataNews(date) <= takeDate(-30))
    }

    sealed class SearchState {
        data class SetSources(val list: List<Sources>) : SearchState()
        data class SetHistory(val list: List<HistorySelect>) : SearchState()
        data class PikerPositive(val l: Long) : SearchState()
        object HideSelectHistory : SearchState()
        object SearchInShow : SearchState()
        object EmptyHistory : SearchState()
        object PikerNegative : SearchState()
        object SelectSources : SearchState()
        object ErrorDateInput : SearchState()
        object SourcesInShow : SearchState()
        object HideEmptyList : SearchState()
    }
}