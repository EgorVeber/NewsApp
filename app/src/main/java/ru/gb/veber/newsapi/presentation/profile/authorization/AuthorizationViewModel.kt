package ru.gb.veber.newsapi.presentation.profile.authorization

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.gb.veber.newsapi.core.AccountScreen
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.domain.interactor.AuthorizationInteractor
import ru.gb.veber.newsapi.domain.models.AccountModel
import ru.gb.veber.newsapi.presentation.base.NewsViewModel
import ru.gb.veber.ui_common.ALL_COUNTRY
import ru.gb.veber.ui_common.ALL_COUNTRY_VALUE
import ru.gb.veber.ui_common.API_KEY_NEWS
import ru.gb.veber.ui_common.TAG_DB_ERROR
import ru.gb.veber.ui_common.coroutine.launchJob
import ru.gb.veber.ui_common.getCutLogin
import ru.gb.veber.ui_common.utils.AuthPattern.EMAIL_PATTERN
import ru.gb.veber.ui_common.utils.AuthPattern.LOGIN_PATTERN
import ru.gb.veber.ui_common.utils.AuthPattern.PASSWORD_PATTERN
import java.util.Date
import javax.inject.Inject

class AuthorizationViewModel @Inject constructor(
    private val router: Router,
    private val authorizationInteractor: AuthorizationInteractor,
) : NewsViewModel() {

    private val mutableFlow: MutableLiveData<AuthorizationViewState> = MutableLiveData()
    private val flow: LiveData<AuthorizationViewState> = mutableFlow

    private val logInState: MutableSharedFlow<Pair<Boolean, Int>> = MutableSharedFlow(replay = 1)
    val logInFlow: SharedFlow<Pair<Boolean, Int>> = logInState.asSharedFlow()

    private val signInState: MutableSharedFlow<Int> = MutableSharedFlow(replay = 1)
    val signInFlow: SharedFlow<Int> = signInState.asSharedFlow()

    fun subscribe(): LiveData<AuthorizationViewState> {
        return flow
    }

    override fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun createAccount(username: String, email: String, password: String) {
        viewModelScope.launchJob(tryBlock = {
            authorizationInteractor.createAccount(getNewAccount(username, password, email))
            val account = authorizationInteractor.getAccountByUserNameV2(username)
            saveIdSharedPref(account)
            authorizationInteractor.setActiveKey(API_KEY_NEWS)//TODO убрать говнише
            logInState.emit(Pair(true, account.id))

        }, catchBlock = { error ->
            mutableFlow.postValue(AuthorizationViewState.ErrorRegister)
            Log.d(TAG_DB_ERROR, error.localizedMessage)
        })
    }

    fun checkSignIn(userLogin: String, userPassword: String) {
        viewModelScope.launchJob(tryBlock = {
            val account = authorizationInteractor.getAccountByUserNameV2(userLogin)
            if (account.password.contains(userPassword)) {
                val apikeyModel =
                    authorizationInteractor.getApiKeys(account.id).sortedBy { !it.actived }
                if (apikeyModel.isNotEmpty()) {
                    val apiKey = apikeyModel[0]
                    if (apiKey.actived) authorizationInteractor.setActiveKey(apikeyModel[0].keyApi)//TODO убрать говнише
                }
                signInState.emit(account.id)
                saveIdSharedPref(account)
            } else {
                mutableFlow.postValue(AuthorizationViewState.ErrorSignIn)
            }
        }, catchBlock = { error ->
            Log.d(TAG_DB_ERROR, error.localizedMessage)
            mutableFlow.postValue(AuthorizationViewState.EmptyAccount)
        })
    }

    private fun saveIdSharedPref(accountModel: AccountModel) {
        authorizationInteractor.setAccountID(accountModel.id)
        authorizationInteractor.setAccountLogin(accountModel.userName.getCutLogin())
        mutableFlow.postValue(
            AuthorizationViewState.SetBottomNavigationIcon(accountModel.userName.getCutLogin())
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

    private fun getNewAccount(
        username: String,
        password: String,
        email: String,
    ): AccountModel =
        AccountModel(
            id = 0,
            userName = username,
            password = password,
            email = email,
            createdAt = Date().toString(),
            saveHistory = true,
            saveSelectHistory = true,
            displayOnlySources = false,
            myCountry = ALL_COUNTRY,
            countryCode = ALL_COUNTRY_VALUE
        )

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
