package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.AllNewsScreen
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.HistorySelect
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.database.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepoImpl
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.HistorySelectRepoImpl
import ru.gb.veber.newsapi.model.repository.room.SourcesRepoImpl
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.sources.FragmentSourcesView

class SourcesPresenter(
    private val accountIdPresenter: Int,
    private val router: Router,
    private val accountSourcesRepoImpl: AccountSourcesRepoImpl,
    private val sourcesRepoImpl: SourcesRepoImpl,
    private val articleRepoImpl: ArticleRepoImpl,
    private val historySelectRepoImpl: HistorySelectRepoImpl,
) :
    MvpPresenter<FragmentSourcesView>() {

    private lateinit var allSources: MutableList<Sources>
    private lateinit var likeSources: List<Sources>

    fun onBackPressedRouter(): Boolean {
        Log.d("Back", "onBackPressedRouter() SourcesDTO")
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
                    all.forEach { sor ->
                        article.forEach { art ->
                            if (sor.idSources == art.sourceId) {
                                if (art.isFavorites) sor.totalFavorites += 1
                                else sor.totalHistory += 1
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

    fun openWebView(url: String) {
        router.navigateTo(WebViewScreen(url))
    }

    fun imageClick(source: Sources) {
        if (source.isLike) {
            source.isLike = false
            accountSourcesRepoImpl.deleteSourcesLike(accountIdPresenter, source.id).subscribe({
                getSources()
                Log.d("DbImageClick", "success deleteSourcesLike")
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        } else {
            source.isLike = true
            accountSourcesRepoImpl.insert(AccountSourcesDbEntity(accountIdPresenter, source.id))
                .subscribe({
                    getSources()
                    Log.d("DbImageClick", "success insert")
                }, {
                    Log.d(ERROR_DB, it.localizedMessage)
                })
        }
    }

    fun openAllNews(source: String?, name: String?) {
        router.navigateTo(AllNewsScreen(accountIdPresenter,
            HistorySelect(0, accountIdPresenter, sourcesId = source, sourcesName = name)))
        var x = HistorySelect(
            0, accountID = accountIdPresenter,
            sourcesId = source,
            sourcesName = name,
        )
        historySelectRepoImpl.insertSelect(mapToHistorySelectDbEntity(x))
            .subscribe({}, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
    }
}