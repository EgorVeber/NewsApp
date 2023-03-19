package ru.gb.veber.newsapi.view.profile.authorization

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.gb.veber.newsapi.common.extentions.EMAIL_PATTERN
import ru.gb.veber.newsapi.common.extentions.LOGIN_PATTERN
import ru.gb.veber.newsapi.common.extentions.PASSWORD_PATTERN
import ru.gb.veber.newsapi.common.extentions.checkLogin
import ru.gb.veber.newsapi.common.extentions.launchJob
import ru.gb.veber.newsapi.common.screen.AccountScreen
import ru.gb.veber.newsapi.common.screen.WebViewScreen
import ru.gb.veber.newsapi.common.utils.ALL_COUNTRY
import ru.gb.veber.newsapi.common.utils.ALL_COUNTRY_VALUE
import ru.gb.veber.newsapi.common.utils.ERROR_DB
import ru.gb.veber.newsapi.data.SharedPreferenceAccount
import ru.gb.veber.newsapi.data.models.room.entity.AccountDbEntity
import ru.gb.veber.newsapi.domain.models.Account
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import java.util.*
import javax.inject.Inject

class AuthorizationViewModel @Inject constructor(
    private val router: Router,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val accountRepoImpl: AccountRepo,
) : ViewModel() {

    private val mutableFlow: MutableLiveData<AuthorizationViewState> = MutableLiveData()
    private val flow: LiveData<AuthorizationViewState> = mutableFlow

    private val logInState: MutableSharedFlow<Pair<Boolean, Int>> = MutableSharedFlow(replay = 1)
    val logInFlow: SharedFlow<Pair<Boolean, Int>> = logInState.asSharedFlow()

    private val signInState: MutableSharedFlow<Int> = MutableSharedFlow(replay = 1)
    val signInFlow: SharedFlow<Int> = signInState.asSharedFlow()

    fun subscribe(): LiveData<AuthorizationViewState> {
        return flow
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun createAccount(username: String, email: String, password: String) {
        viewModelScope.launchJob(tryBlock = {
            val accountDbEntity = AccountDbEntity(
                0,
                username,
                password,
                email,
                Date().toString(),
                saveHistory = true,
                saveSelectHistory = true,
                displayOnlySources = false,
                myCountry = ALL_COUNTRY,
                countryCode = ALL_COUNTRY_VALUE
            )
            accountRepoImpl.createAccountV2(accountDbEntity)
            val account = accountRepoImpl.getAccountByUserNameV2(username)
            saveIdSharedPref(account)

            logInState.emit(Pair(true, account.id))

        }, catchBlock = { error ->
            mutableFlow.postValue(AuthorizationViewState.ErrorRegister)
            Log.d(ERROR_DB, error.localizedMessage)
        })
    }

    fun checkSignIn(userLogin: String, userPassword: String) {
        viewModelScope.launchJob(tryBlock = {
            val account = accountRepoImpl.getAccountByUserNameV2(userLogin)
            if (account.password.contains(userPassword)) {
                signInState.emit(account.id)
                saveIdSharedPref(account)
            } else {
                mutableFlow.postValue(AuthorizationViewState.ErrorSignIn)
            }
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
            mutableFlow.postValue(AuthorizationViewState.EmptyAccount)
        })
    }

    private fun saveIdSharedPref(account: Account) {
        sharedPreferenceAccount.setAccountID(account.id)
        sharedPreferenceAccount.setAccountLogin(account.userName.checkLogin())
        mutableFlow.postValue(AuthorizationViewState.SetBottomNavigationIcon(account.userName.checkLogin())
        )
    }

    fun openScreenProfile(id: Int) {
        router.replaceScreen(AccountScreen(id))
    }

    fun openMain() {
        mutableFlow.value = AuthorizationViewState.SendActivityOpenScreen
    }

    fun openScreenWebView(string: String) {
        router.navigateTo(WebViewScreen(string))
    }

    fun changeRegisterAnim() {
        mutableFlow.value = AuthorizationViewState.SetRegisterAnim
    }

    fun changeLoginAnim() {
        mutableFlow.value = AuthorizationViewState.SetLoginAnim
    }

    fun loginValidation(login: CharSequence?) {
        if (LOGIN_PATTERN.matcher(login).matches()) {
            mutableFlow.value = AuthorizationViewState.LoginIsValidate(login)
        } else {
            mutableFlow.value = AuthorizationViewState.LoginNotValidate
        }
    }

    fun passwordValidation(password: CharSequence?) {
        if (PASSWORD_PATTERN.matcher(password).matches()) {
            mutableFlow.value = AuthorizationViewState.PasswordIsValidate(password)
        } else {
            mutableFlow.value = AuthorizationViewState.PasswordNotValidate(password)
        }
    }

    fun passwordRegisterValidation(password: CharSequence?) {
        if (PASSWORD_PATTERN.matcher(password).matches()) {
            mutableFlow.value = AuthorizationViewState.PasswordRegisterIsValidate(password)
        } else {
            mutableFlow.value = AuthorizationViewState.PasswordRegisterNotValidate(password)
        }
    }

    fun loginRegisterValidation(login: CharSequence?) {
        if (LOGIN_PATTERN.matcher(login).matches()) {
            mutableFlow.value = AuthorizationViewState.LoginRegisterIsValidate(login)
        } else {
            mutableFlow.value = AuthorizationViewState.LoginRegisterNotValidate
        }
    }

    fun emailRegisterValidation(email: CharSequence?) {
        if (EMAIL_PATTERN.matcher(email).matches()) {
            mutableFlow.value = AuthorizationViewState.EmailRegisterIsValidate(email)
        } else {
            mutableFlow.value = AuthorizationViewState.EmailRegisterNotValidate
        }
    }

    sealed class AuthorizationViewState {
        data class LoginIsValidate(val charSequence: CharSequence?) : AuthorizationViewState()
        data class PasswordIsValidate(val password: CharSequence?) : AuthorizationViewState()
        data class PasswordNotValidate(val password: CharSequence?) : AuthorizationViewState()
        data class LoginRegisterIsValidate(val login: CharSequence?) : AuthorizationViewState()
        data class PasswordRegisterIsValidate(val password: CharSequence?) :
            AuthorizationViewState()

        data class PasswordRegisterNotValidate(val password: CharSequence?) :
            AuthorizationViewState()

        data class EmailRegisterIsValidate(val email: CharSequence?) : AuthorizationViewState()
        data class SetBottomNavigationIcon(val checkLogin: String) : AuthorizationViewState()

        object ErrorSignIn : AuthorizationViewState()
        object ErrorRegister : AuthorizationViewState()
        object SendActivityOpenScreen : AuthorizationViewState()
        object SetRegisterAnim : AuthorizationViewState()
        object SetLoginAnim : AuthorizationViewState()
        object LoginNotValidate : AuthorizationViewState()
        object LoginRegisterNotValidate : AuthorizationViewState()
        object EmailRegisterNotValidate : AuthorizationViewState()
        object EmptyAccount : AuthorizationViewState()
    }
}
