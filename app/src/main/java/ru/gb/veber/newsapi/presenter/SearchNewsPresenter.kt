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
import ru.gb.veber.newsapi.utils.sourcesDbEntityToSources
import ru.gb.veber.newsapi.view.searchnews.SearchNewsView

class SearchNewsPresenter(
    private val newsRepoImpl: NewsRepoImpl,
    private val router: Router,
    private val articleRepoImpl: ArticleRepoImpl,
    private val roomRepoImpl: RoomRepoImpl,
    private val accountId: Int,
    private val sourcesRepoImpl: SourcesRepoImpl,
    private val accountSourcesRepoImpl: AccountSourcesRepoImpl,
) :
    MvpPresenter<SearchNewsView>() {

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

    fun openSearchNews(it: String) {

    }

    fun getSources() {
        sourcesRepoImpl.getSources().subscribe({
            listSources = it
            viewState.setSources(it)
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }
}