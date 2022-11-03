package ru.gb.veber.newsapi.presenter

import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.AccountScreen
import ru.gb.veber.newsapi.core.AuthorizationScreen
import ru.gb.veber.newsapi.view.profile.ProfileView
import javax.inject.Inject

class ProfilePresenter() :
    MvpPresenter<ProfileView>() {

    @Inject lateinit var router: Router
    fun openScreenProfile(accountId: Int) {
        router.replaceScreen(AccountScreen(accountId))
    }

    fun openScreenAuthorization() {
        router.replaceScreen(AuthorizationScreen)
    }
}