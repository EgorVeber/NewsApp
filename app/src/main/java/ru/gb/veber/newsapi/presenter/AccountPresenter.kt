package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.AuthorizationScreen
import ru.gb.veber.newsapi.core.EditAccountScreen
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.ACCOUNT_LOGIN_DEFAULT
import ru.gb.veber.newsapi.utils.ERROR_DB
import ru.gb.veber.newsapi.view.profile.account.AccountView

class AccountPresenter(
    private val router: Router,
    private val roomRepoImpl: RoomRepoImpl,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
) :
    MvpPresenter<AccountView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        Log.d("Back", "onBackPressedRouter() Profile")
        return true
    }

    fun logout() {
        router.replaceScreen(AuthorizationScreen)
        sharedPreferenceAccount.setAccountID(ACCOUNT_ID_DEFAULT)
        sharedPreferenceAccount.setAccountLogin(ACCOUNT_LOGIN_DEFAULT)
    }

    fun openScreenEditAccount(accountID: Int) {
        router.navigateTo(EditAccountScreen(accountID))
    }

    fun deleteAccount(accountID: Int) {
        if (accountID != ACCOUNT_ID_DEFAULT) {
            roomRepoImpl.deleteAccount(accountID).subscribe({
                logout()
                viewState.setBottomNavigationIcon()
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        }
    }

    fun getAccountSettings(accountID: Int?) {
        viewState.loading()
        accountID?.let {
            roomRepoImpl.getAccountById(it).subscribe({ account ->
                viewState.setAccountInfo(account)
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        }
    }

    fun setBottomNavigationIcon() {
        viewState.setBottomNavigationIcon()
    }

    fun showDialog() {
        viewState.showDialog()
    }
}