package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl

class FragmentNewsPresenter(private val newsRepoImpl: NewsRepoImpl,private val router: Router) :
    MvpPresenter<FragmentNewsView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
        newsRepoImpl.getTopicalHeadlines().subscribe({
            Log.d("TAG", "onFirstViewAttach() called")
            Log.d("TAG", it.status.toString())
            Log.d("TAG", it.totalResults.toString())
            Log.d("TAG", it.articles.toString())
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