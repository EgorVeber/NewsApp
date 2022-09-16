package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.view.newsitem.FragmentNewsView

class FragmentNewsPresenter(
    private val newsRepoImpl: NewsRepoImpl,
    private val router: Router,
    private val category: String,
) :
    MvpPresenter<FragmentNewsView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
        //newsRepoDataBaseGetDefaultCategory
        //Какая нибусь сложная штука с любымими источниками или категориями
        //А можно просто по дефолку новости любимой страны например
        Log.d("category", category)
        newsRepoImpl.getTopicalHeadlinesCountryCategory("ru", category).map { articles ->
            articles.articles.map { it.editRequest() }
            articles
        }.subscribe({
            Log.d("TAG", "onFirstViewAttach() called")
            Log.d("TAG", it.status)
            Log.d("category", it.totalResults.toString())
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