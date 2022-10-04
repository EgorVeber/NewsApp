package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.AuthorizationScreen
import ru.gb.veber.newsapi.core.EditAccountScreen
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.ACCOUNT_LOGIN_DEFAULT
import ru.gb.veber.newsapi.utils.ERROR_DB
import ru.gb.veber.newsapi.utils.subscribeDefault
import ru.gb.veber.newsapi.view.profile.account.AccountView

class AccountPresenter(
    private val router: Router,
    private val roomRepoImpl: RoomRepoImpl,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val articleRepoImpl: ArticleRepoImpl,
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
        accountID?.let { acc ->
            Single.zip(roomRepoImpl.getAccountById(acc),
                articleRepoImpl.getArticleById(accountID)) { account, articles ->
                account.totalFavorites = articles.filter { it.isFavorites }.size.toString()
                account.totalHistory = articles.filter { it.isHistory }.size.toString()
                account
            }.subscribeDefault().subscribe({
                viewState.setAccountInfo(it)
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
//            roomRepoImpl.getAccountById(it).subscribe({ account ->
//                viewState.setAccountInfo(account)
//            }, {
//                Log.d(ERROR_DB, it.localizedMessage)
//            })
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
}