package ru.gb.veber.newsapi.presentation.favorites.viewpager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import javax.inject.Inject

class FavoritesViewPagerViewModel @Inject constructor(private val router: Router) : ViewModel() {

    private val mutableFlow: MutableLiveData<FavoritesViewPagerState> = MutableLiveData()
    private val flow: LiveData<FavoritesViewPagerState> = mutableFlow

    fun subscribe(): LiveData<FavoritesViewPagerState> {
        return flow
    }

    fun init() {
        mutableFlow.value = FavoritesViewPagerState.InitView
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    sealed class FavoritesViewPagerState() {
        object InitView : FavoritesViewPagerState()
    }
}

