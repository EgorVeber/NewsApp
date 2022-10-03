package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.profile.account.settings.EditAccountView

class EditAccountPresenter(
    private val router: Router,
    private val roomRepoImpl: RoomRepoImpl,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
) : MvpPresenter<EditAccountView>() {

    private lateinit var account: Account

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun getAccountDataBase(accountId: Int) {
        viewState.loading()
        roomRepoImpl.getAccountById(accountId).subscribe({
            account = it
            viewState.setAccountDate(it)
        }, {
            Log.d(ERROR_DB, it.localizedMessage)
            viewState.errorLoadingAccount()
        })
    }

    fun backAccountScreen() {
        router.exit()
    }

    fun passwordValidation(it: CharSequence?) {
        if (PASSWORD_PATTERN.matcher(it).matches()) {
            viewState.passwordIsValidate(it)
        } else {
            viewState.passwordNotValidate(it)
        }
    }

    fun loginValidation(it: CharSequence?) {
        if (LOGIN_PATTERN.matcher(it).matches()) {
            viewState.loginIsValidate(it)
        } else {
            viewState.loginNotValidate()
        }
    }

    fun emailRegisterValidation(it: CharSequence?) {
        if (EMAIL_PATTERN.matcher(it).matches()) {
            viewState.emailRegisterIsValidate(it)
        } else {
            viewState.emailRegisterNotValidate()
        }
    }

    fun checkSaveAccount(userLogin: String, userPassword: String, userEmail: String) {

        if (account.userName == userLogin && account.email == userEmail && account.password == userPassword) {
            viewState.noChangeAccount()
        } else {
            account.email = userEmail
            account.userName = userLogin
            account.password = userPassword
            roomRepoImpl.updateAccount(mapToAccountDbEntity(account)).subscribe({
                viewState.successUpdateAccount(account.userName.checkLogin())
                sharedPreferenceAccount.setAccountLogin(account.userName.checkLogin())
            }, {
                viewState.errorUpdateAccount()
                Log.d(ERROR_DB, it.localizedMessage)
            })
        }
    }
}