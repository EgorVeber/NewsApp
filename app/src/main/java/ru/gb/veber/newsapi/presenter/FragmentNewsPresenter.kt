package ru.gb.veber.newsapi.presenter

import android.util.Log
import moxy.MvpPresenter
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl

class FragmentNewsPresenter(private val newsRepoImpl: NewsRepoImpl) :
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
}