package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import moxy.MvpPresenter
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.model.repository.room.SourcesRepoImpl
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.ERROR_DB
import ru.gb.veber.newsapi.view.searchnews.SearchNewsView

class SearchNewsPresenter(
    private val router: Router,
    private val roomRepoImpl: RoomRepoImpl,
    private val accountId: Int,
    private val sourcesRepoImpl: SourcesRepoImpl,
    private val accountSourcesRepoImpl: AccountSourcesRepoImpl,
) : MvpPresenter<SearchNewsView>() {

    private lateinit var allSources: List<Sources>
    private lateinit var likeSources: List<Sources>
    private var accountHistorySelect: Boolean = false
    private lateinit var accountMain: Account

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun openSearchNews(it: String) {

    }

    fun getSources() {
        if (accountId == ACCOUNT_ID_DEFAULT) {
            viewState.hideSelectHistory()
            sourcesRepoImpl.getSources().subscribe({
                viewState.setSources(it)
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        } else {
            Single.zip(sourcesRepoImpl.getSources(),
                accountSourcesRepoImpl.getLikeSourcesFromAccount(accountId),
                roomRepoImpl.getAccountById(accountId)) { all, like, account ->

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
                allSources = it
                viewState.setSources(it)
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        }
    }

    fun notifyAdapter(b: Boolean) {
        if(b){
            viewState.updateAdapter(allSources)

        }else{
            viewState.updateAdapter(likeSources)
        }
    }
}