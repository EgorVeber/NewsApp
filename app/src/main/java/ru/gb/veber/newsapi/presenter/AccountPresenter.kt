package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.AuthorizationScreen
import ru.gb.veber.newsapi.core.EditAccountScreen
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.profile.account.AccountView

class AccountPresenter(
    private val router: Router,
    private val roomRepoImpl: RoomRepoImpl,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val articleRepoImpl: ArticleRepoImpl,
) :
    MvpPresenter<AccountView>() {

    private var accountId = ACCOUNT_ID_DEFAULT
    private lateinit var accountMain: Account


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
        //sharedPreferenceAccount.setAccountCountry(ALL_COUNTRY_VALUE)
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
        accountID?.let { acc ->
            accountId = accountID
            Single.zip(roomRepoImpl.getAccountById(acc),
                articleRepoImpl.getArticleById(accountID)) { account, articles ->
                accountMain = account
                account.totalFavorites = articles.filter { it.isFavorites }.size.toString()
                account.totalHistory = articles.filter { it.isHistory }.size.toString()
                account
            }.subscribeDefault().subscribe({
                viewState.setAccountInfo(it)
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

    fun clearHistory(accountId: Int) {
        articleRepoImpl.deleteArticleIsHistoryById(accountId).subscribe({
            viewState.clearHistory()
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }

    fun clearFavorites(accountId: Int) {
        articleRepoImpl.deleteArticleIsFavoriteById(accountId).subscribe({
            viewState.clearFavorites()
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }

    fun updateAccountSaveHistory(checked: Boolean) {
        roomRepoImpl.updateAccountById(accountId, checked).subscribe({
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }

    fun updateAccountShowListFavorite(b: Boolean) {
        accountMain?.let {
            it.displayOnlySources = b
            roomRepoImpl.updateAccount(mapToAccountDbEntity(it)).subscribe({
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        }
    }

    fun updateAccountSaveHistorySelect(checked: Boolean) {
        accountMain.saveSelectHistory = checked
        roomRepoImpl.updateAccount(mapToAccountDbEntity(accountMain)).subscribe({
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }
}