package ru.gb.veber.newsapi.view.profile.account


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.common.extentions.launchJob
import ru.gb.veber.newsapi.common.screen.AuthorizationScreen
import ru.gb.veber.newsapi.common.screen.CustomizeCategoryScreen
import ru.gb.veber.newsapi.common.screen.EditAccountScreen
import ru.gb.veber.newsapi.common.screen.WebViewScreen
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.ACCOUNT_LOGIN_DEFAULT
import ru.gb.veber.newsapi.common.utils.ERROR_DB
import ru.gb.veber.newsapi.common.utils.KEY_THEME_DARK
import ru.gb.veber.newsapi.common.utils.KEY_THEME_DEFAULT
import ru.gb.veber.newsapi.data.SharedPreferenceAccount
import ru.gb.veber.newsapi.data.mapper.toAccountDbEntity
import ru.gb.veber.newsapi.domain.models.Account
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.AccountSourcesRepo
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
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

            viewModelScope.launchJob(tryBlock = {
                accountRepo.deleteAccountV2(accountID)
                logout()
                _uiState.postValue(AccountViewState.SetBottomNavigationIcon)
            }, catchBlock = { error ->
                Log.d(ERROR_DB, error.localizedMessage)
            })
        }
    }


    fun getAccountSettings(accountID: Int?) {
        _uiState.value = AccountViewState.Loading
        accountID?.let { acc ->
            accountId = accountID
            viewModelScope.launchJob(tryBlock = {
                val account = accountRepo.getAccountByIdV2(acc)
                val articles = articleRepoImpl.getArticleByIdV2(accountID)
                val listSources = accountSourcesRepoImpl.getLikeSourcesFromAccountV2(accountID)
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
                _uiState.postValue(AccountViewState.SetAccountInfo(
                    account,
                    sharedPreferenceAccount.getThemePrefs()
                ))

            }, catchBlock = { error ->
                Log.d(ERROR_DB, error.localizedMessage)
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
        viewModelScope.launchJob(tryBlock = {
            articleRepoImpl.deleteArticleIsHistoryByIdV2(accountId)
            _uiState.postValue(AccountViewState.ClearHistory)
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    fun clearFavorites(accountId: Int) {
        viewModelScope.launchJob(tryBlock = {
            articleRepoImpl.deleteArticleIsFavoriteByIdV2(accountId)
            _uiState.postValue(AccountViewState.ClearFavorites)
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    fun clearSources(accountId: Int) {
        viewModelScope.launchJob(tryBlock = {
            accountSourcesRepoImpl.deleteSourcesV2(accountId)
            _uiState.postValue(AccountViewState.ClearSources)
        }, catchBlock = {error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }


    fun updateAccountSaveHistory(checked: Boolean) {
        viewModelScope.launchJob(tryBlock = {
            accountRepo.updateAccountByIdV2(accountId, checked)
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)

        })
    }

    fun updateAccountShowListFavorite(b: Boolean) {
        accountMain?.let { account ->
            account.displayOnlySources = b
            viewModelScope.launchJob(tryBlock = {
                accountRepo.updateAccountV2(account.toAccountDbEntity())
            }, catchBlock = { error ->
                Log.d(ERROR_DB, error.localizedMessage)
            })
        }
    }

    fun updateAccountSaveHistorySelect(checked: Boolean) {
        accountMain.saveSelectHistory = checked
        viewModelScope.launchJob(tryBlock = {
            accountRepo.updateAccountV2(accountMain.toAccountDbEntity())
        }, catchBlock = {error ->
            Log.d(ERROR_DB, error.localizedMessage)
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
