package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import moxy.MvpPresenter
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
import ru.gb.veber.newsapi.utils.mapToHistorySelectDbEntity
import ru.gb.veber.newsapi.view.sources.FragmentSourcesView
import javax.inject.Inject

class SourcesPresenter(
    private val accountIdPresenter: Int,
) :
    MvpPresenter<FragmentSourcesView>() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var accountSourcesRepoImpl: AccountSourcesRepo

    @Inject
    lateinit var sourcesRepoImpl: SourcesRepo

    @Inject
    lateinit var articleRepoImpl: ArticleRepo

    @Inject
    lateinit var historySelectRepoImpl: HistorySelectRepo


    private lateinit var allSources: MutableList<Sources>
    private lateinit var likeSources: List<Sources>


    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun getSources() {
        if (accountIdPresenter == ACCOUNT_ID_DEFAULT) {
            sourcesRepoImpl.getSources().subscribe({
                allSources = it
                viewState.setSources(it)
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        } else {
            Single.zip(
                sourcesRepoImpl.getSources(),
                accountSourcesRepoImpl.getLikeSourcesFromAccount(accountIdPresenter),
                articleRepoImpl.getArticleById(accountIdPresenter)
            ) { all, like, article ->
                allSources = all
                like.map { it.isLike = true }
                likeSources = like

                for (j in like.size - 1 downTo 0) {
                    for (i in all.indices) {
                        if (like[j].idSources == all[i].idSources) {
                            all.removeAt(i)
                            all.add(0, like[j].also { it.isLike = true })
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
            }.subscribe({
                viewState.setSources(it)
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        }
    }

    fun openWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
    }

    fun imageClick(source: Sources) {
        if (accountIdPresenter != ACCOUNT_ID_DEFAULT) {
            if (source.isLike) {
                source.isLike = false
                accountSourcesRepoImpl.deleteSourcesLike(accountIdPresenter, source.id).subscribe({
                    getSources()
                }, {
                    Log.d(ERROR_DB, it.localizedMessage)
                })
            } else {
                source.isLike = true
                accountSourcesRepoImpl.insert(AccountSourcesDbEntity(accountIdPresenter, source.id))
                    .subscribe({
                        getSources()
                    }, {
                        Log.d(ERROR_DB, it.localizedMessage)
                    })
            }
        } else {
            viewState.setLogin()
        }
    }

    //TODO NewsAndroid-11 Рефакторинг SourcesPresenter (Убрать нулы)
    fun openAllNews(source: String?, name: String?) {
        val history = HistorySelect(0, accountID = accountIdPresenter, sourcesId = source, sourcesName = name)
        router.navigateTo(SearchNewsScreen(accountIdPresenter, history))
        historySelectRepoImpl.insertSelect(mapToHistorySelectDbEntity(history))
            .subscribe({}, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
    }
}