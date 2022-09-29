package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity
import ru.gb.veber.newsapi.model.repository.RoomRepoImpl
import ru.gb.veber.newsapi.presenter.FragmentProfileMainPresenter.Companion.TEST_BUNDLE
import ru.gb.veber.newsapi.utils.LOGIN_PATTERN
import ru.gb.veber.newsapi.utils.LOGIN_STR
import ru.gb.veber.newsapi.utils.PASSWORD_PATTERN
import ru.gb.veber.newsapi.utils.PASSWORD_STR
import ru.gb.veber.newsapi.view.profile.FragmentAuthorizationView
import java.util.*

class FragmentAuthorizationPresenter(
    private val router: Router,
    private val roomRepoImpl: RoomRepoImpl,
) :
    MvpPresenter<FragmentAuthorizationView>() {

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
            .subscribe({
                viewState.success()
            }, {
                viewState.error()
                Log.d("ERROR_DB", it.localizedMessage)
            })
    }

    fun openScreenProfile() {
        router.navigateTo(FragmentProfileScreen(TEST_BUNDLE))
    }

    fun openMain() {
        viewState.sendActivityOpenScreen()
    }

    fun openScreenWebView(string: String) {
        router.navigateTo(FragmentNewsWebViewScreen(string))
    }

    fun changeRegisterAnim() {
        viewState.setRegisterAnim()
    }

    fun changeLoginAnim() {
        viewState.setLoginAnim()
    }

    fun getPasswordToLogin(userLogin: String, userPassword: String) {
        roomRepoImpl.getAccountByUserName(userLogin)
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
}