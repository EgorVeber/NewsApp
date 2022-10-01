package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.disposables.CompositeDisposable
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.WebViewScreen
import ru.gb.veber.newsapi.core.FragmentProfileScreen
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.model.setAccountID
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.profile.authorization.AuthorizationView
import java.util.*

class AuthorizationPresenter(
    private val router: Router,
    private val roomRepoImpl: RoomRepoImpl,
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
        roomRepoImpl.createAccount(AccountDbEntity(0, username, password, email, Date().toString()))
            .andThen(roomRepoImpl.getAccountByUserName(username)).subscribe({
                Log.d("ERROR_DB", it.toString())
                viewState.successRegister(it.id)
                saveIdSharedPref(it.id)
            }, {
                viewState.errorRegister()
                Log.d("ERROR_DB", it.localizedMessage)
            }).disposebleBy(bag)
    }

    fun checkSignIn(userLogin: String, userPassword: String) {
        roomRepoImpl.getAccountByUserName(userLogin).subscribe({
            if (it.password.contains(userPassword)) {
                viewState.successSignIn(it.id)
                saveIdSharedPref(it.id)
            } else {
                viewState.errorSignIn()
            }
        }, {
            Log.d("ERROR_DB", it.localizedMessage)
            viewState.emptyAccount()
        }).disposebleBy(bag)
    }

    private fun saveIdSharedPref(accountId: Int) {
        setAccountID(accountId)
    }

    fun openScreenProfile(id: Int) {
        router.replaceScreen(FragmentProfileScreen(id))
    }

    fun openMain() {
        viewState.sendActivityOpenScreen()
       // router.replaceScreen(AllNewsScreen(0))
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