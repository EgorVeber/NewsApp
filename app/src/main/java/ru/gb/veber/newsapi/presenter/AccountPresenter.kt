package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.AuthorizationScreen
import ru.gb.veber.newsapi.core.EditAccountScreen
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.room.AccountRepoImpl
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepoImpl
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.profile.account.AccountView

class AccountPresenter(
    private val router: Router,
    private val accountRepo: AccountRepoImpl,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val articleRepoImpl: ArticleRepoImpl,
    private val accountSourcesRepoImpl: AccountSourcesRepoImpl,
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
            accountRepo.deleteAccount(accountID).subscribe({
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
            Single.zip(accountRepo.getAccountById(acc),
                articleRepoImpl.getArticleById(accountID),
                accountSourcesRepoImpl.getLikeSourcesFromAccount(accountID)) { account, articles, listSources ->

                accountMain = account
                account.totalFavorites = articles.filter { it.isFavorites }.size.toString()
                account.totalHistory = articles.filter { it.isHistory }.size.toString()
                account.totalSources = listSources.size.toString()
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

    fun clearSources(accountId: Int) {
        accountSourcesRepoImpl.deleteSources(accountId).subscribe({
            viewState.clearSources()
        }, {
        })
    }


    fun updateAccountSaveHistory(checked: Boolean) {
        accountRepo.updateAccountById(accountId, checked).subscribe({
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }

    fun updateAccountShowListFavorite(b: Boolean) {
        accountMain?.let {
            it.displayOnlySources = b
            accountRepo.updateAccount(mapToAccountDbEntity(it)).subscribe({
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        }
    }

    fun updateAccountSaveHistorySelect(checked: Boolean) {
        accountMain.saveSelectHistory = checked
        accountRepo.updateAccount(mapToAccountDbEntity(accountMain)).subscribe({
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
        })
    }


}