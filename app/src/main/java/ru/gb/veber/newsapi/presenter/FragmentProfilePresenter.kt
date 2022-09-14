package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter

class FragmentProfilePresenter(private val router: Router) :
    MvpPresenter<FragmentProfileView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }
    fun onBackPressedRouter(): Boolean {
        router.backTo(FragmentNewsScreen)
        Log.d("Back", "onBackPressedRouter() Profile")
        return true
    }
}