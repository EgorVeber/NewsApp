package ru.gb.veber.newsapi.view.profile.authorization

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.gb.veber.newsapi.core.AccountScreen
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity
import ru.gb.veber.newsapi.model.repository.room.AccountRepo
import ru.gb.veber.newsapi.utils.ALL_COUNTRY
import ru.gb.veber.newsapi.utils.ALL_COUNTRY_VALUE
import ru.gb.veber.newsapi.utils.ERROR_DB
import ru.gb.veber.newsapi.utils.disposableBy
import ru.gb.veber.newsapi.utils.checkLogin
import ru.gb.veber.newsapi.utils.LOGIN_PATTERN
import ru.gb.veber.newsapi.utils.PASSWORD_PATTERN
import ru.gb.veber.newsapi.utils.EMAIL_PATTERN
import java.util.Date
import javax.inject.Inject

class AuthorizationViewModel @Inject constructor(
    private val router: Router,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val accountRepoImpl: AccountRepo
) : ViewModel() {

    private val mutableFlow: MutableLiveData<AuthorizationViewState> = MutableLiveData()
    private val flow: LiveData<AuthorizationViewState> = mutableFlow

    private val bag = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        bag.dispose()
    }

    fun subscribe(accountId: Int): LiveData<AuthorizationViewState> {
        return flow
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun createAccount(username: String, email: String, password: String) {
        accountRepoImpl.createAccount(
            AccountDbEntity(
                0,
                username,
                password,
                email,
                Date().toString(),
                saveHistory = true,
                saveSelectHistory = true,
                displayOnlySources = false,
                myCountry = ALL_COUNTRY, countryCode = ALL_COUNTRY_VALUE
            )
        )
            .andThen(accountRepoImpl.getAccountByUserName(username)).subscribe({ account ->
                mutableFlow.value = AuthorizationViewState.SuccessRegister(account.id)
                saveIdSharedPref(account)
            }, { error ->
                mutableFlow.value = AuthorizationViewState.ErrorRegister
                Log.d(ERROR_DB, error.localizedMessage)
            }).disposableBy(bag)
    }

    fun checkSignIn(userLogin: String, userPassword: String) {
        accountRepoImpl.getAccountByUserName(userLogin).subscribe({ account ->
            if (account.password.contains(userPassword)) {
                mutableFlow.value = AuthorizationViewState.SuccessSignIn(account.id)
                saveIdSharedPref(account)
            } else {
                mutableFlow.value = AuthorizationViewState.ErrorSignIn
            }
        }, { error ->
            Log.d(ERROR_DB, error.localizedMessage)
            mutableFlow.value = AuthorizationViewState.EmptyAccount
        }).disposableBy(bag)
    }

    private fun saveIdSharedPref(accountId: Account) {
        sharedPreferenceAccount.setAccountID(accountId.id)
        sharedPreferenceAccount.setAccountLogin(accountId.userName.checkLogin())
        mutableFlow.value =
            AuthorizationViewState.SetBottomNavigationIcon(accountId.userName.checkLogin())
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

        data class SuccessRegister(val id: Int) : AuthorizationViewState()
        data class LoginIsValidate(val charSequence: CharSequence?) : AuthorizationViewState()
        data class PasswordIsValidate(val password: CharSequence?) : AuthorizationViewState()
        data class PasswordNotValidate(val password: CharSequence?) : AuthorizationViewState()
        data class LoginRegisterIsValidate(val login: CharSequence?) : AuthorizationViewState()
        data class SuccessSignIn(val id: Int) : AuthorizationViewState()
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