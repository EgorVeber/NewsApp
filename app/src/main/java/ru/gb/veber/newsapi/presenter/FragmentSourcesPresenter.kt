package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl

class FragmentSourcesPresenter(private val newsRepoImpl: NewsRepoImpl,private val router: Router) :
    MvpPresenter<FragmentSourcesView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
        newsRepoImpl.getSources().subscribe({
            Log.d("TAG", "onFirstViewAttach() called")
            Log.d("TAG", it.status.toString())
            Log.d("TAG", it.sources.toString())
            viewState.setSources(it.sources)
        }, {
            Log.d("TAG", it.localizedMessage)
        })
    }
    fun onBackPressedRouter(): Boolean {
        Log.d("Back", "onBackPressedRouter() SourcesDTO")
        router.backTo(FragmentNewsScreen)
        return true
    }
}