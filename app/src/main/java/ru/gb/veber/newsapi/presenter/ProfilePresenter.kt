package ru.gb.veber.newsapi.presenter

import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.FragmentAuthorizationScreen
import ru.gb.veber.newsapi.core.FragmentProfileScreen
import ru.gb.veber.newsapi.view.profile.ProfileView

class ProfilePresenter(private val router: Router) :
    MvpPresenter<ProfileView>() {

    fun openScreenProfile(accountId: Int) {
        router.replaceScreen(FragmentProfileScreen(accountId))
    }

    fun openScreenAuthorization() {
        router.replaceScreen(FragmentAuthorizationScreen)
    }
}