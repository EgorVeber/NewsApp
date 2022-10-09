package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.AllNewsScreen
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.model.repository.room.SourcesRepoImpl
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.searchnews.SearchNewsView
import java.util.*

class SearchNewsPresenter(
    private val router: Router,
    private val roomRepoImpl: RoomRepoImpl,
    private val accountId: Int,
    private val sourcesRepoImpl: SourcesRepoImpl,
    private val accountSourcesRepoImpl: AccountSourcesRepoImpl,
) : MvpPresenter<SearchNewsView>() {

    private lateinit var allSources: MutableList<Sources>
    private lateinit var likeSources: List<Sources>
    private var accountHistorySelect: Boolean = false
    private lateinit var accountMain: Account

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }


    fun getSources() {
        if (accountId == ACCOUNT_ID_DEFAULT) {
            viewState.hideSelectHistory()
            sourcesRepoImpl.getSources().subscribe({

                allSources = it
                viewState.setSources(it)
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        } else {
            Single.zip(sourcesRepoImpl.getSources(),
                accountSourcesRepoImpl.getLikeSourcesFromAccount(accountId),
                roomRepoImpl.getAccountById(accountId)) { all, like, account ->

                allSources = all

                like.map {
                    it.isLike = true
                }

                likeSources = like

                if (!account.saveSelectHistory) {
                    viewState.hideSelectHistory()
                    accountHistorySelect = account.saveSelectHistory
                }

                accountMain = account
                if (account.displayOnlySources && like.isNotEmpty()) {
                    Log.d("displayOnlySources",
                        "getSources() called with: all = $all, like = $like, account = $account")
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

    fun notifyAdapter(b: Boolean) {
        if (b) {
            viewState.updateAdapter(allSources)

        } else {
            viewState.updateAdapter(likeSources)
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
                var sources = allSources.find { it.name == sourcesName }?.idSources
                if (date == NOT_INPUT_DATE) {
                    router.navigateTo(AllNewsScreen(accountId = accountId,
                        sourcesName = sources,
                        sortBySources = sortBy))
                } else {
                    router.navigateTo(AllNewsScreen(accountId = accountId,
                        sourcesName = sources,
                        sortBySources = sortBy,
                        dateSources = date))
                }
                //router.navigateTo(AllNewsScreen)
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
            router.navigateTo(AllNewsScreen(accountId = accountId,
                keyWord = keyWord,
                searchIn = searchIn,
                sortByKeyWord = sortBy))
        } else {
            if (!allSources.map { it.name }.contains(sourcesName)) {
                viewState.selectSources()
            } else {
                var sources = allSources.find { it.name == sourcesName }?.idSources
                router.navigateTo(AllNewsScreen(accountId = accountId,
                    keyWord = keyWord,
                    searchIn = searchIn,
                    sortByKeyWord = sortBy, sourcesName = sources))
            }
        }
    }
}
