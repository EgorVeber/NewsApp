package ru.gb.veber.newsapi.view.topnews.viewpager

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import javax.inject.Inject

class TopNewsViewPagerViewModel @Inject constructor(private val router: Router) : ViewModel() {

    fun onBackPressedRouter(): Boolean {
        router.exit()

        return true
    }

}