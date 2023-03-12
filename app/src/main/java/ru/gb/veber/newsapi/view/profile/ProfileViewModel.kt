package ru.gb.veber.newsapi.view.profile

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.common.screen.AccountScreen
import ru.gb.veber.newsapi.common.screen.AuthorizationScreen
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val router: Router,
) : ViewModel() {


    fun openScreenProfile(accountId: Int) {
        router.replaceScreen(AccountScreen(accountId))
    }

    fun openScreenAuthorization() {
        router.replaceScreen(AuthorizationScreen)
    }
}