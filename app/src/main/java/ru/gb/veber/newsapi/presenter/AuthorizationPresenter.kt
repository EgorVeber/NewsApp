package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.disposables.CompositeDisposable
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.AccountScreen
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.profile.authorization.AuthorizationView
import java.util.*

class AuthorizationPresenter(
    private val router: Router,
    private val roomRepoImpl: RoomRepoImpl,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
) :
    MvpPresenter<AuthorizationView>() {

    private val bag = CompositeDisposable()

    override fun onFirstViewAttach() {
        Log.d("TAG", "onFirstViewAttach() called")
        viewState.init()
        super.onFirstViewAttach()
    }

    fun onBackPressedRouter(): Boolean {
        Log.d("@@@", "onBackPressedRouter() ActivityPresenter")
        router.exit()
        return true
    }

    fun createAccount(username: String, email: String, password: String) {
        roomRepoImpl.createAccount(AccountDbEntity(0,
            username,
            password,
            email,
            Date().toString(),
            saveHistory = true,
            saveSelectHistory = true,
            displayOnlySources = false,
            myCountry = ALL_COUNTRY, countryCode = ALL_COUNTRY_VALUE))
            .andThen(roomRepoImpl.getAccountByUserName(username)).subscribe({
                viewState.successRegister(it.id)
                saveIdSharedPref(it)
            }, {
                viewState.errorRegister()
                Log.d(ERROR_DB, it.localizedMessage)
            }).disposebleBy(bag)
    }

    fun checkSignIn(userLogin: String, userPassword: String) {
        roomRepoImpl.getAccountByUserName(userLogin).subscribe({
            if (it.password.contains(userPassword)) {
                viewState.successSignIn(it.id)
                saveIdSharedPref(it)
            } else {
                viewState.errorSignIn()
            }
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
            viewState.emptyAccount()
        }).disposebleBy(bag)
    }

    private fun saveIdSharedPref(accountId: Account) {
        sharedPreferenceAccount.setAccountID(accountId.id)
        sharedPreferenceAccount.setAccountLogin(accountId.userName.checkLogin())
        // sharedPreferenceAccount.setAccountCountry(accountId.myCountry)
        viewState.setBottomNavigationIcon(accountId.userName.checkLogin())
    }

    fun openScreenProfile(id: Int) {
        router.replaceScreen(AccountScreen(id))
    }

    fun openMain() {
        viewState.sendActivityOpenScreen()
    }

    fun openScreenWebView(string: String) {
        router.navigateTo(WebViewScreen(string))
    }

    fun changeRegisterAnim() {
        viewState.setRegisterAnim()
    }

    fun changeLoginAnim() {
        viewState.setLoginAnim()
    }


    fun loginValidation(it: CharSequence?) {
        if (LOGIN_PATTERN.matcher(it).matches()) {
            viewState.loginIsValidate(it)
        } else {
            viewState.loginNotValidate()
        }
    }

    fun passwordValidation(it: CharSequence?) {
        if (PASSWORD_PATTERN.matcher(it).matches()) {
            viewState.passwordIsValidate(it)
        } else {
            viewState.passwordNotValidate(it)
        }
    }

    fun passwordRegisterValidation(it: CharSequence?) {
        if (PASSWORD_PATTERN.matcher(it).matches()) {
            viewState.passwordRegisterIsValidate(it)
        } else {
            viewState.passwordRegisterNotValidate(it)
        }
    }

    fun loginRegisterValidation(it: CharSequence?) {
        if (LOGIN_PATTERN.matcher(it).matches()) {
            viewState.loginRegisterIsValidate(it)
        } else {
            viewState.loginRegisterNotValidate()
        }
    }

    fun emailRegisterValidation(it: CharSequence?) {
        if (EMAIL_PATTERN.matcher(it).matches()) {
            viewState.emailRegisterIsValidate(it)
        } else {
            viewState.emailRegisterNotValidate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bag.dispose()
    }
}