package ru.gb.veber.newsapi.presentation.profile.account.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.common.base.NewsViewModel
import ru.gb.veber.newsapi.common.extentions.AuthPattern.EMAIL_PATTERN
import ru.gb.veber.newsapi.common.extentions.AuthPattern.LOGIN_PATTERN
import ru.gb.veber.newsapi.common.extentions.AuthPattern.PASSWORD_PATTERN
import ru.gb.veber.newsapi.common.extentions.checkLogin
import ru.gb.veber.newsapi.common.extentions.launchJob
import ru.gb.veber.newsapi.common.utils.ERROR_DB
import ru.gb.veber.newsapi.domain.interactor.EditAccountInteractor
import ru.gb.veber.newsapi.domain.models.AccountModel
import javax.inject.Inject

class EditAccountViewModel @Inject constructor(
    private val router: Router,
    private val editAccountInteractor: EditAccountInteractor,
) : NewsViewModel() {

    private val mutableFlow: MutableLiveData<EditAccountState> = MutableLiveData()
    private val flow: LiveData<EditAccountState> = mutableFlow

    private lateinit var accountModel: AccountModel

    fun subscribe(accountId: Int): LiveData<EditAccountState> {
        getAccountDataBase(accountId)
        return flow
    }

    fun checkSaveAccount(userLogin: String, userPassword: String, userEmail: String) {
        if (accountModel.userName == userLogin && accountModel.email == userEmail && accountModel.password == userPassword) {
            mutableFlow.value = EditAccountState.NoChangeAccount
        } else {
            accountModel.email = userEmail
            accountModel.userName = userLogin
            accountModel.password = userPassword
            viewModelScope.launchJob(tryBlock = {
                editAccountInteractor.updateAccount(accountModel, accountModel.userName.checkLogin())
                mutableFlow.postValue(EditAccountState.SuccessUpdateAccount(accountModel.userName.checkLogin()))
            }, catchBlock = { error ->
                mutableFlow.postValue(EditAccountState.ErrorUpdateAccount)
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

    override fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    private fun getAccountDataBase(accountId: Int) {
        mutableFlow.value = EditAccountState.Loading
        viewModelScope.launchJob(tryBlock = {
            val accountDb = editAccountInteractor.getAccount(accountId)
            accountModel = accountDb
            mutableFlow.postValue(EditAccountState.SetAccountDate(accountModel))
        }, catchBlock = { error ->
            Log.d(ERROR_DB, error.localizedMessage)
            mutableFlow.postValue(EditAccountState.ErrorLoadingAccount)
        })
    }

    sealed class EditAccountState {
        data class SetAccountDate(val accountModel: AccountModel) : EditAccountState()
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
