package ru.gb.veber.newsapi.presenter

import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import moxy.MvpView
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.room.AccountRepo
import javax.inject.Inject

class CustomizeCategoryPresenter : MvpPresenter<MvpView>() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var sharedPreferenceAccount: SharedPreferenceAccount

    @Inject
    lateinit var accountRepo: AccountRepo

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun backAccountScreen() {
        router.exit()
    }
}