package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.utils.mapToArticle
import ru.gb.veber.newsapi.view.allnews.AllNewsView

class AllNewsPresenter(
    private val newsRepoImpl: NewsRepoImpl,
    private val router: Router,
    private val articleRepoImpl: ArticleRepoImpl,
    private val roomRepoImpl: RoomRepoImpl,
) :
    MvpPresenter<AllNewsView>() {
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
        newsRepoImpl.getEverythingKeyWordSearchIn(query).subscribe({articles->
            viewState.setNews(articles.articles.map(::mapToArticle).also {
                newsRepoImpl.changeRequest(it)
            })
        }, {

        })
    }
}