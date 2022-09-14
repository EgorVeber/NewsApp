package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.view.news.FragmentNewsView

class FragmentNewsPresenter(private val newsRepoImpl: NewsRepoImpl, private val router: Router) :
    MvpPresenter<FragmentNewsView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
        //newsRepoDataBaseGetDefaultCategory
        //Какая нибусь сложная штука с любымими источниками или категориями
        //А можно просто по дефолку новости любимой страны например
        newsRepoImpl.getTopicalHeadlinesCountry("ru").map {articles->
            articles.articles.map { it.editRequest() }
            articles
        }.subscribe({
            Log.d("TAG", "onFirstViewAttach() called")
            Log.d("TAG", it.status)
            Log.d("TAG", it.totalResults.toString())
            Log.d("TAG", it.articles.toString())
            viewState.setSources(it.articles)
        }, {
            Log.d("TAG", it.localizedMessage)
        })
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        Log.d("Back", "onBackPressedRouter() News")
        return true
    }
}