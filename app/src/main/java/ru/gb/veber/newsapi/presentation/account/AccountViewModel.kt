package ru.gb.veber.newsapi.presentation.account

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.common.base.NewsViewModel
import ru.gb.veber.newsapi.common.extentions.launchJob
import ru.gb.veber.newsapi.common.screen.AuthorizationScreen
import ru.gb.veber.newsapi.common.screen.CustomizeCategoryScreen
import ru.gb.veber.newsapi.common.screen.EditAccountScreen
import ru.gb.veber.newsapi.common.screen.FileScreen
import ru.gb.veber.newsapi.common.screen.KeyManagementScreen
import ru.gb.veber.newsapi.common.screen.WebViewScreen
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.ERROR_DB
import ru.gb.veber.newsapi.common.utils.KEY_THEME_DARK
import ru.gb.veber.newsapi.common.utils.KEY_THEME_DEFAULT
import ru.gb.veber.newsapi.domain.interactor.AccountInteractor
import ru.gb.veber.newsapi.domain.models.AccountModel
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val router: Router,
    private val accountInteractor: AccountInteractor,
) : NewsViewModel() {

    private val mutableState: MutableLiveData<AccountViewState> = MutableLiveData()
    private val flow: LiveData<AccountViewState> = mutableState

    private var accountId: Int = ACCOUNT_ID_DEFAULT
    private lateinit var accountModelMain: AccountModel

    fun subscribe(accountId: Int): LiveData<AccountViewState> {
        this.accountId = accountId
        getAccountSettings()
        return flow
    }

    override fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    private fun getAccountSettings() {
        mutableState.value = AccountViewState.Loading
        viewModelScope.launchJob(tryBlock = {
            val account = accountInteractor.getAccountByIdV2(accountId)
            val articles = accountInteractor.getArticleByIdV2(accountId)
            val listSources = accountInteractor.getLikeSourcesFromAccountV2(accountId)
            accountModelMain = account
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
            mutableState.postValue(
                AccountViewState.SetAccountInfo(
                    account,
                    accountInteractor.getThemePrefs()
                )
            )

        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    fun logout() {
        router.replaceScreen(AuthorizationScreen)
        accountInteractor.accountLogout()
    }

    fun openScreenEditAccount() {
        router.navigateTo(EditAccountScreen(accountId))
    }

    fun openScreenCustomizeCategory() {
        router.navigateTo(CustomizeCategoryScreen(accountId))
    }

    fun deleteAccount() {
        if (accountId != ACCOUNT_ID_DEFAULT) {

            viewModelScope.launchJob(tryBlock = {
                accountInteractor.deleteAccountV2(accountId)
                logout()
                mutableState.postValue(AccountViewState.SetBottomNavigationIcon)
            }, catchBlock = { error ->
                Log.d(ERROR_DB, error.localizedMessage)
            })
        }
    }

    fun setStateSetBottomNavigationIcon() {
        mutableState.value = AccountViewState.SetBottomNavigationIcon
    }

    fun setStateShowDialog(title: String, message: String, positive: String, onClick: () -> Unit) {
        mutableState.value =
            AccountViewState.AccountDialog(
                title = title,
                message = message,
                positive = positive,
                onClick = onClick
            )
    }

    fun clearHistory() {
        viewModelScope.launchJob(tryBlock = {
            accountInteractor.deleteArticleIsHistoryByIdV2(accountId)
            mutableState.postValue(AccountViewState.ClearHistory)
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    fun clearFavorites() {
        viewModelScope.launchJob(tryBlock = {
            accountInteractor.deleteArticleIsFavoriteByIdV2(accountId)
            mutableState.postValue(AccountViewState.ClearFavorites)
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    fun clearSources() {
        viewModelScope.launchJob(tryBlock = {
            accountInteractor.deleteSourcesV2(accountId)
            mutableState.postValue(AccountViewState.ClearSources)
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }


    fun updateAccountSaveHistory(checked: Boolean) {
        viewModelScope.launchJob(tryBlock = {
            accountInteractor.updateAccountByIdV2(accountId, checked)
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)

        })
    }

    fun updateAccountShowListFavorite(b: Boolean) {
        accountModelMain?.let { account ->
            account.displayOnlySources = b
            viewModelScope.launchJob(tryBlock = {
                accountInteractor.updateAccount(account)
            }, catchBlock = { error ->
                Log.d(ERROR_DB, error.localizedMessage)
            })
        }
    }

    fun updateAccountSaveHistorySelect(checked: Boolean) {
        accountModelMain.saveSelectHistory = checked
        viewModelScope.launchJob(tryBlock = {
            accountInteractor.updateAccount(accountModelMain)
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    fun setTheme(b: Boolean) {
        mutableState.value = AccountViewState.ToastDelete
        accountInteractor.setTheme(if (b) KEY_THEME_DARK else KEY_THEME_DEFAULT)
        mutableState.value = AccountViewState.RecreateTheme
    }

    fun openScreenWebView(string: String) {
        router.navigateTo(WebViewScreen(string))
    }

    fun openScrenKeysManagement(accountId: Int) {
        router.navigateTo(KeyManagementScreen(accountId))
    }

    fun onClickFileItem(accountId: Int) {
        router.navigateTo(FileScreen(accountId))
    }

    sealed class AccountViewState {
        object Loading : AccountViewState()
        data class SetAccountInfo(val accountModel: AccountModel, val theme: Int) : AccountViewState()
        object SetBottomNavigationIcon : AccountViewState()
        data class AccountDialog(
            val title: String,
            val message: String,
            val positive: String,
            val onClick: () -> Unit,
        ) : AccountViewState()

        object ClearHistory : AccountViewState()
        object ClearFavorites : AccountViewState()
        object ClearSources : AccountViewState()
        object ToastDelete : AccountViewState()
        object RecreateTheme : AccountViewState()
    }
}
