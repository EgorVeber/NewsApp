package ru.gb.veber.newsapi.view.topnews.viewpager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import javax.inject.Inject

class TopNewsViewPagerViewModel @Inject constructor(private val router: Router) : ViewModel() {

    private val mutableFlow: MutableLiveData<TopNewsViewPagerState> = MutableLiveData()
    private val flow: LiveData<TopNewsViewPagerState> = mutableFlow

    fun subscribe(): LiveData<TopNewsViewPagerState> {
        return flow
    }

    fun init() {
        mutableFlow.value = TopNewsViewPagerState.InitView
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    sealed class TopNewsViewPagerState() {
        object InitView : TopNewsViewPagerState()
    }
}