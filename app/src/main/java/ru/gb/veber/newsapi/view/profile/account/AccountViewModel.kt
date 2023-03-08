package ru.gb.veber.newsapi.view.profile.account

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.core.AuthorizationScreen
import ru.gb.veber.newsapi.core.CustomizeCategoryScreen
import ru.gb.veber.newsapi.core.EditAccountScreen
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.room.AccountRepo
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepo
import ru.gb.veber.newsapi.model.repository.room.ArticleRepo
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.utils.extentions.subscribeDefault
import ru.gb.veber.newsapi.utils.mapper.toAccountDbEntity
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val router: Router,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val accountRepo: AccountRepo,
    private val articleRepoImpl: ArticleRepo,
    private val accountSourcesRepoImpl: AccountSourcesRepo,
) : ViewModel() {

    private val _uiState: MutableLiveData<AccountViewState> = MutableLiveData()
    private val uiState: LiveData<AccountViewState> = _uiState

    fun getLiveData(): LiveData<AccountViewState> {
        return uiState
    }

    private var accountId = ACCOUNT_ID_DEFAULT
    private lateinit var accountMain: Account

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

    fun openScreenCustomizeCategory(accountId: Int) {
        router.navigateTo(CustomizeCategoryScreen(accountId))
    }

    fun deleteAccount(accountID: Int) {
        if (accountID != ACCOUNT_ID_DEFAULT) {
            accountRepo.deleteAccount(accountID).subscribe({
                logout()
                _uiState.value = AccountViewState.SetBottomNavigationIcon
            }, { throwable ->
                Log.d(ERROR_DB, throwable.localizedMessage)
            })
        }
    }

    fun getAccountSettings(accountID: Int?) {
        _uiState.value = AccountViewState.Loading
        accountID?.let { acc ->
            accountId = accountID
            Single.zip(
                accountRepo.getAccountById(acc),
                articleRepoImpl.getArticleById(accountID),
                accountSourcesRepoImpl.getLikeSourcesFromAccount(accountID)
            ) { account, articles, listSources ->

                accountMain = account
                account.totalFavorites = articles.filter { article ->
                    article.isFavorites
                }.size.toString()
                account.totalHistory =
                    articles.filter { article ->
                        !article.isFavorites
                    }.filter { article ->
                        article.isHistory
                    }.size.toString()
                account.totalSources = listSources.size.toString()
                account
            }.subscribeDefault().subscribe({ account ->
                _uiState.value =
                    AccountViewState.SetAccountInfo(
                        account,
                        sharedPreferenceAccount.getThemePrefs()
                    )
            }, { throwable ->
                Log.d(ERROR_DB, throwable.localizedMessage)
            })
        }
    }

    fun setStateSetBottomNavigationIcon() {
        _uiState.value = AccountViewState.SetBottomNavigationIcon
    }

    fun setStateShowDialog(title: String, message: String, positive: String, onClick: () -> Unit) {
        _uiState.value =
            AccountViewState.AccountDialog(
                title = title,
                message = message,
                positive = positive,
                onClick = onClick
            )
    }

    fun clearHistory(accountId: Int) {
        articleRepoImpl.deleteArticleIsHistoryById(accountId).subscribe({
            _uiState.value = AccountViewState.ClearHistory
        }, { throwable ->
            Log.d(ERROR_DB, throwable.localizedMessage)
        })
    }

    fun clearFavorites(accountId: Int) {
        articleRepoImpl.deleteArticleIsFavoriteById(accountId).subscribe({
            _uiState.value = AccountViewState.ClearFavorites
        }, { throwable ->
            Log.d(ERROR_DB, throwable.localizedMessage)
        })
    }

    fun clearSources(accountId: Int) {
        accountSourcesRepoImpl.deleteSources(accountId).subscribe({
            _uiState.value = AccountViewState.ClearSources
        }, {
        })
    }


    fun updateAccountSaveHistory(checked: Boolean) {
        accountRepo.updateAccountById(accountId, checked).subscribe({
        }, { throwable ->
            Log.d(ERROR_DB, throwable.localizedMessage)
        })
    }

    fun updateAccountShowListFavorite(b: Boolean) {
        accountMain?.let { account ->
            account.displayOnlySources = b
            accountRepo.updateAccount(account.toAccountDbEntity()).subscribe({
            }, { throwable ->
                Log.d(ERROR_DB, throwable.localizedMessage)
            })
        }
    }

    fun updateAccountSaveHistorySelect(checked: Boolean) {
        accountMain.saveSelectHistory = checked
        accountRepo.updateAccount(accountMain.toAccountDbEntity()).subscribe({
        }, { throwable ->
            Log.d(ERROR_DB, throwable.localizedMessage)
        })
    }

    fun setTheme(b: Boolean) {
        _uiState.value = AccountViewState.ToastDelete
        sharedPreferenceAccount.setTheme(if (b) KEY_THEME_DARK else KEY_THEME_DEFAULT)
        _uiState.value = AccountViewState.RecreateTheme
    }

    fun openScreenWebView(string: String) {
        router.navigateTo(WebViewScreen(string))
    }
}

sealed class AccountViewState {
    object Loading : AccountViewState()
    data class SetAccountInfo(val account: Account, val theme: Int) : AccountViewState()
    object SetBottomNavigationIcon : AccountViewState()
    data class AccountDialog(
        val title: String,
        val message: String,
        val positive: String,
        val onClick: () -> Unit
    ) : AccountViewState()

    object ClearHistory : AccountViewState()
    object ClearFavorites : AccountViewState()
    object ClearSources : AccountViewState()
    object ToastDelete : AccountViewState()
    object RecreateTheme : AccountViewState()
}