package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.SearchNewsScreen
import ru.gb.veber.newsapi.model.HistorySelect
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.repository.room.AccountRepo
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepo
import ru.gb.veber.newsapi.model.repository.room.HistorySelectRepo
import ru.gb.veber.newsapi.model.repository.room.SourcesRepo
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.search.SearchView
import java.util.*
import javax.inject.Inject

class SearchPresenter(
    private val accountIdPresenter: Int,
) : MvpPresenter<SearchView>() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var accountRepoImpl: AccountRepo

    @Inject
    lateinit var historySelectRepoImpl: HistorySelectRepo

    @Inject
    lateinit var sourcesRepoImpl: SourcesRepo

    @Inject
    lateinit var accountSourcesRepoImpl: AccountSourcesRepo

    private lateinit var allSources: MutableList<Sources>
    private lateinit var likeSources: List<Sources>
    private var accountHistorySelect: Boolean = false


    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun getSources() {
        if (accountIdPresenter == ACCOUNT_ID_DEFAULT) {
            viewState.hideSelectHistory()
            viewState.hideEmptyList()
            sourcesRepoImpl.getSources().subscribe({

                allSources = it
                viewState.setSources(it)
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        } else {
            Single.zip(sourcesRepoImpl.getSources(),
                accountSourcesRepoImpl.getLikeSourcesFromAccount(accountIdPresenter),
                accountRepoImpl.getAccountById(accountIdPresenter)) { all, like, account ->

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
                viewState.setSources(it)
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        }
    }

    fun changeSearchCriteria(b: Boolean) {
        if (!b) {
            viewState.searchInShow()
        } else {
            viewState.sourcesInShow()
        }
    }

    fun openScreenAllNewsSources(
        date: String,
        sourcesName: String,
        sortBy: String,
    ) {
        if (sourcesName.isEmpty() || !allSources.map { it.name }.contains(sourcesName)) {
            viewState.selectSources()
        } else {
            if (!checkDate(date)) {
                viewState.errorDateInput()
            } else {
                var sourcesId = allSources.find { it.name == sourcesName }?.idSources
                if (date == NOT_INPUT_DATE) {
                    var x = HistorySelect(
                        0, accountID = accountIdPresenter,
                        sourcesId = sourcesId,
                        sortBySources = sortBy,
                        sourcesName = sourcesName,
                    )
                    router.navigateTo(SearchNewsScreen(accountIdPresenter, x))

                    if (accountHistorySelect) {
                        historySelectRepoImpl.insertSelect(mapToHistorySelectDbEntity(x))
                            .subscribe({}, {
                                Log.d(ERROR_DB, it.localizedMessage)
                            })
                    }
                } else {
                    var x = HistorySelect(
                        0, accountID = accountIdPresenter,
                        sourcesId = sourcesId,
                        sortBySources = sortBy,
                        sourcesName = sourcesName,
                        dateSources = stringFromDataPiker(date).formatDate()
                    )

                    router.navigateTo(SearchNewsScreen(accountIdPresenter, x))
                    if (accountHistorySelect) {
                        historySelectRepoImpl.insertSelect(mapToHistorySelectDbEntity(x))
                            .subscribe({}, {
                                Log.d(ERROR_DB, it.localizedMessage)
                            })
                    }
                }
            }
        }
    }

    private fun checkDate(date: String): Boolean {
        if (date == NOT_INPUT_DATE) {
            return true
        }
        return !(stringFromDataNews(date) > Date() || stringFromDataNews(date) <= takeDate(-30))
    }

    fun openScreenAllNews(
        keyWord: String,
        searchIn: String,
        sortBy: String,
        sourcesName: String,
    ) {
        if (sourcesName.isEmpty()) {

            var x = HistorySelect(id = 0, accountID = accountIdPresenter,
                keyWord = keyWord,
                searchIn = searchIn,
                sortByKeyWord = sortBy)


            router.navigateTo(SearchNewsScreen(accountIdPresenter, x))

            if (accountHistorySelect) {
                historySelectRepoImpl.insertSelect(mapToHistorySelectDbEntity(x)).subscribe({}, {
                    Log.d(ERROR_DB, it.localizedMessage)
                })
            }
        } else {
            if (!allSources.map { it.name }.contains(sourcesName)) {
                viewState.selectSources()
            } else {
                var sourcesId = allSources.find { it.name == sourcesName }?.idSources

                var x = HistorySelect(
                    0, accountID = accountIdPresenter,
                    keyWord = keyWord,
                    searchIn = searchIn,
                    sortByKeyWord = sortBy,
                    sourcesId = sourcesId,
                    sourcesName = sourcesName
                )
                router.navigateTo(SearchNewsScreen(accountIdPresenter, x))

                if (accountHistorySelect) {
                    historySelectRepoImpl.insertSelect(mapToHistorySelectDbEntity(x))
                        .subscribe({}, {
                            Log.d(ERROR_DB, it.localizedMessage)
                        })
                }
            }
        }
    }

    fun pikerPositive(l: Long) {
        viewState.pikerPositive(l)
    }

    fun pikerNegative() {
        viewState.pikerNegative()
    }

    fun getHistorySelect() {
        if (accountIdPresenter != ACCOUNT_ID_DEFAULT) {
            historySelectRepoImpl.getHistoryById(accountIdPresenter).subscribe({
                if (it.isEmpty()) {
                    viewState.setHistory(listOf())
                    viewState.emptyHistory()
                } else {
                    viewState.setHistory(it.map(::mapToHistorySelect).reversed())
                }
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        }
    }

    fun openScreenNewsHistory(historySelect: HistorySelect) {
        router.navigateTo(SearchNewsScreen(accountIdPresenter, historySelect))
    }

    fun clearHistory() {
        historySelectRepoImpl.deleteSelectById(accountIdPresenter).subscribe({
            viewState.setHistory(listOf())
            viewState.emptyHistory()
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }

    fun deleteHistory(historySelect: HistorySelect) {
        historySelectRepoImpl.deleteSelect(mapToHistorySelectDbEntity(historySelect)).subscribe({
            getHistorySelect()
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }
}
