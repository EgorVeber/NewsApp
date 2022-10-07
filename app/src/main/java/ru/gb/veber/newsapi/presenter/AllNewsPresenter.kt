package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
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
        sourcesRepoImpl.getSources().subscribe({
            listSources = it.map(::sourcesDbEntityToSources)
            viewState.setSources(it.map(::sourcesDbEntityToSources))
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }

    fun loadNewsSources(text: String) {

        var x = listSources.filter { it.name == text }
        x.forEach {
            Log.d("TAG", it.toString())
        }

        newsRepoImpl.getEverythingKeyWordSearchInSources(x[0].idSources!!).subscribe({ articles ->
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