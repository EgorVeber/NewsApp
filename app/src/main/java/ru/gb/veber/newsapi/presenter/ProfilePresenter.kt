package ru.gb.veber.newsapi.presenter

import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.AccountScreen
import ru.gb.veber.newsapi.core.AuthorizationScreen
import ru.gb.veber.newsapi.view.profile.ProfileView

class ProfilePresenter(private val router: Router) :
    MvpPresenter<ProfileView>() {

    fun openScreenProfile(accountId: Int) {
        router.replaceScreen(AccountScreen(accountId))
    }

    fun openScreenAuthorization() {
        router.replaceScreen(AuthorizationScreen)
    }
}