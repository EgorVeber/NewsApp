package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.utils.mapToArticle
import ru.gb.veber.newsapi.view.newsitem.FragmentNewsView

class FragmentNewsPresenter(
    private val newsRepoImpl: NewsRepoImpl,
    private val router: Router,
) :
    MvpPresenter<FragmentNewsView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        Log.d("Back", "onBackPressedRouter() News")
        return true
    }

    fun clickNews(it: Article) {
        viewState.clickNews(it)
    }

    fun loadNews(category: String) {
        //newsRepoDataBaseGetDefaultCategory
        //Какая нибусь сложная штука с любымими источниками или категориями
        //А можно просто по дефолку новости любимой страны например
        newsRepoImpl.getTopicalHeadlinesCategoryCountry(category,"ru").map { articles ->
            articles.articles.map(::mapToArticle).also {
                newsRepoImpl.changeRequest(it)
            }
        }.subscribe({
            viewState.setSources(it)
        }, {
            Log.d("TAG", it.localizedMessage)
        })
    }

    fun loadNewsCountry(country: String) {
        newsRepoImpl.getTopicalHeadlinesCountryCategory(country).map { articles ->
            articles.articles.map(::mapToArticle).also {
                newsRepoImpl.changeRequest(it)
            }
        }.subscribe({
            viewState.setSources(it)
        }, {
            Log.d("TAG", it.localizedMessage)
        })
    }
}