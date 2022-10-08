package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import moxy.MvpPresenter
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepoImpl
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.model.repository.room.SourcesRepoImpl
import ru.gb.veber.newsapi.utils.ERROR_DB
import ru.gb.veber.newsapi.utils.mapToArticle
import ru.gb.veber.newsapi.utils.sourcesDbEntityToSources
import ru.gb.veber.newsapi.utils.sourcesDtoToEntity
import ru.gb.veber.newsapi.view.allnews.AllNewsView
import java.util.*

class AllNewsPresenter(
    private val newsRepoImpl: NewsRepoImpl,
    private val router: Router,
    private val articleRepoImpl: ArticleRepoImpl,
    private val roomRepoImpl: RoomRepoImpl,
    private val accountId: Int,
    private val sourcesRepoImpl: SourcesRepoImpl,
    private val accountSourcesRepoImpl: AccountSourcesRepoImpl,
) :
    MvpPresenter<AllNewsView>() {

    private lateinit var listSources: List<Sources>
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun onBackPressedRouter(): Boolean {
        Log.d("Back", "onBackPressedRouter() SourcesDTO")
        router.exit()
        return true
    }

    fun loadNews(query: String) {
        viewState.loading()
        newsRepoImpl.getEverythingKeyWordSearchIn(query).subscribe({ articles ->
            viewState.setNews(articles.articles.map(::mapToArticle).also {
                if (it.isNotEmpty()) {
                    newsRepoImpl.changeRequest(it)
                }
            })
        }, {

        })
    }

    fun getSources() {
        Single.zip(sourcesRepoImpl.getSources(),
            accountSourcesRepoImpl.getLikeSourcesFromAccount(accountId)) { all, like ->
            like.map { lik ->
                for (i in all.indices) {
                    if (lik.idSources == all[i].idSources) {
                        all.removeAt(i)
                        all.add(0, lik.also { it.isLike=true })
                    }
                }
                //                all.map { al ->
//                    if (lik.idSources == al.idSources) {
//                        al.isLike = true
////                        Collections.swap(all,1,2 )
//                    }
//                }
            }
            all
        }.subscribe({
            listSources = it
            viewState.setSources(it)
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })

//        sourcesRepoImpl.getSources().subscribe({
//            viewState.setSources(it)
//        }, {
//            Log.d(ERROR_DB, it.localizedMessage)
//        })
    }

    fun loadNewsSources(text: String) {
        var x = listSources.find { it.name == text }
        newsRepoImpl.getEverythingKeyWordSearchInSources(x?.idSources!!).subscribe({ articles ->
            viewState.setNews(articles.articles.map(::mapToArticle).also {
                if (it.isNotEmpty()) {
                    newsRepoImpl.changeRequest(it)
                }
            })
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }
}