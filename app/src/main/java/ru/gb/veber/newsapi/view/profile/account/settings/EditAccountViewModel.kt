package ru.gb.veber.newsapi.view.profile.account.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.room.AccountRepo
import ru.gb.veber.newsapi.utils.EMAIL_PATTERN
import ru.gb.veber.newsapi.utils.ERROR_DB
import ru.gb.veber.newsapi.utils.LOGIN_PATTERN
import ru.gb.veber.newsapi.utils.PASSWORD_PATTERN
import ru.gb.veber.newsapi.utils.checkLogin
import ru.gb.veber.newsapi.utils.mapToAccountDbEntity
import javax.inject.Inject

class EditAccountViewModel @Inject constructor(
    private val roomRepoImpl: AccountRepo,
    private val router: Router,
    private val prefsAccount: SharedPreferenceAccount,
) : ViewModel() {

    private val mutableFlow: MutableLiveData<EditAccountState> = MutableLiveData()
    private val flow: LiveData<EditAccountState> = mutableFlow

    private lateinit var account: Account

    fun subscribe(accountId: Int): LiveData<EditAccountState> {
        getAccountDataBase(accountId)
        return flow
    }

    fun checkSaveAccount(userLogin: String, userPassword: String, userEmail: String) {
        if (account.userName == userLogin && account.email == userEmail && account.password == userPassword) {
            mutableFlow.value = EditAccountState.NoChangeAccount
        } else {
            account.email = userEmail
            account.userName = userLogin
            account.password = userPassword
            roomRepoImpl.updateAccount(mapToAccountDbEntity(account)).subscribe({
                mutableFlow.value =
                    EditAccountState.SuccessUpdateAccount(account.userName.checkLogin())
                prefsAccount.setAccountLogin(account.userName.checkLogin())
            }, {error->
                mutableFlow.value = EditAccountState.ErrorUpdateAccount
                Log.d(ERROR_DB, error.localizedMessage)
            })
        }
    }


    fun passwordValidation(text: CharSequence?) {
        if (PASSWORD_PATTERN.matcher(text).matches()) {
            mutableFlow.value = EditAccountState.PasswordIsValidate(text)
        } else {
            mutableFlow.value = EditAccountState.PasswordNotValidate
        }
    }

    fun loginValidation(text: CharSequence?) {
        if (LOGIN_PATTERN.matcher(text).matches()) {
            mutableFlow.value = EditAccountState.LoginIsValidate(text)
        } else {
            mutableFlow.value = EditAccountState.LoginNotValidate
        }
    }

    fun emailRegisterValidation(text: CharSequence?) {
        if (EMAIL_PATTERN.matcher(text).matches()) {
            mutableFlow.value = EditAccountState.EmailRegisterIsValidate(text)
        } else {
            mutableFlow.value = EditAccountState.EmailRegisterNotValidate
        }
    }

    fun arrowBack() {
        router.exit()
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    private fun getAccountDataBase(accountId: Int) {
        mutableFlow.value = EditAccountState.Loading
        roomRepoImpl.getAccountById(accountId).subscribe({accountDb->
            account = accountDb
            mutableFlow.value = EditAccountState.SetAccountDate(account)
        }, {error->
            Log.d(ERROR_DB, error.localizedMessage)
            mutableFlow.value = EditAccountState.ErrorLoadingAccount
        })
    }

    sealed class EditAccountState {
        data class SetAccountDate(val account: Account) : EditAccountState()
        object Loading : EditAccountState()
        object ErrorLoadingAccount : EditAccountState()
        data class PasswordIsValidate(val text: CharSequence?) : EditAccountState()
        object PasswordNotValidate : EditAccountState()
        data class LoginIsValidate(val text: CharSequence?) : EditAccountState()
        object LoginNotValidate : EditAccountState()
        data class EmailRegisterIsValidate(val text: CharSequence?) : EditAccountState()
        object EmailRegisterNotValidate : EditAccountState()
        data class SuccessUpdateAccount(val userLogin: String) : EditAccountState()
        object ErrorUpdateAccount : EditAccountState()
        object NoChangeAccount : EditAccountState()
    }
}