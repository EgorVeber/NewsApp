package ru.gb.veber.newsapi.presentation.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.common.screen.AccountScreen
import ru.gb.veber.newsapi.common.screen.AuthorizationScreen
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.BundleInt
import ru.gb.veber.newsapi.core.App
import javax.inject.Inject

class ProfileFragment : Fragment() {

    @Inject
    lateinit var router: Router

    private var accountId by BundleInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.instance.appComponent.inject(this)

        if (accountId != ACCOUNT_ID_DEFAULT) {
            router.replaceScreen(AccountScreen(accountId))
        } else {
            router.replaceScreen(AuthorizationScreen)
        }
    }

    companion object {
        fun getInstance(accountID: Int): ProfileFragment {
            return ProfileFragment().apply {
                this.accountId = accountID
            }
        }
    }
}
