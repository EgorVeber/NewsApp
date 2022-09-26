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

    private var checkFilter = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        Log.d("@@@", "onBackPressedRouter() FragmentNewsPresenter")
        return true
    }

    fun clickNews(it: Article) {
        viewState.clickNews(it)
    }

    fun loadNews(category: String) {
        //newsRepoDataBaseGetDefaultCategory
        //Какая нибусь сложная штука с любымими источниками или категориями
        //А можно просто по дефолку новости любимой страны например
        newsRepoImpl.getTopicalHeadlinesCategoryCountry(category, "ru").map { articles ->
            articles.articles.map(::mapToArticle).also {
                newsRepoImpl.changeRequest(it)
            }
        }.subscribe({
            viewState.setSources(it)
        }, {
            Log.d("TAG", it.localizedMessage)
        })
    }

    fun loadNewsCountry(category: String, country: String) {
        newsRepoImpl.getTopicalHeadlinesCategoryCountry(category, country).map { articles ->
            articles.articles.map(::mapToArticle).also {
                newsRepoImpl.changeRequest(it)
            }
        }.subscribe({
            viewState.setSources(it)
        }, {
            Log.d("TAG", it.localizedMessage)
        })
    }

    fun filterButtonClick() {
        if (!checkFilter) {
            viewState.showFilter()
            checkFilter = true
        } else {
            viewState.hideFilter()
            checkFilter = false
        }
    }

    fun behaviorHide() {
        checkFilter = false
    }

    fun openScreenWebView(url: String) {
        router.navigateTo(FragmentNewsWebViewScreen(url))
    }
}