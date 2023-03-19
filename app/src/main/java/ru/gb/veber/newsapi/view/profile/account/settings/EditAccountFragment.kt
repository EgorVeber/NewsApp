package ru.gb.veber.newsapi.view.profile.account.settings

import android.transition.TransitionManager
import com.jakewharton.rxbinding.widget.RxTextView
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.base.NewsFragment
import ru.gb.veber.newsapi.common.extentions.EMAIL_STR
import ru.gb.veber.newsapi.common.extentions.LOGIN_STR
import ru.gb.veber.newsapi.common.extentions.PASSWORD_STR
import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.common.extentions.showSnackBarError
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.BundleInt
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.EditAccountFragmentBinding
import ru.gb.veber.newsapi.domain.models.Account
import ru.gb.veber.newsapi.presentation.activity.EventLogoutAccountScreen
import ru.gb.veber.newsapi.view.profile.authorization.AuthorizationFragment.Companion.ALFA_HALF_LOGIN_BUTTON
import ru.gb.veber.newsapi.view.profile.authorization.AuthorizationFragment.Companion.ALFA_LOGIN_BUTTON

class EditAccountFragment : NewsFragment<EditAccountFragmentBinding, EditAccountViewModel>(EditAccountFragmentBinding::inflate) {

    private var userLogin: String = ""
    private var userPassword: String = ""
    private var userEmail: String = ""

    private var accountId by BundleInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)

    override fun getViewModelClass(): Class<EditAccountViewModel> = EditAccountViewModel::class.java

    override fun onInject() {
        App.instance.appComponent.inject(this)
    }

    override fun onInitView() {
        initRxTextChangerValidation()
        binding.backAccountScreen.setOnClickListener {
            viewModel.arrowBack()
        }

        binding.saveChangeAccount.setOnClickListener {
            if (checkNullSignUpData()) {
                viewModel.checkSaveAccount(
                    userLogin = userLogin,
                    userPassword = userPassword,
                    userEmail = userEmail
                )
            }
        }
    }

    override fun onObserveData() {
        viewModel.subscribe(accountId).observe(viewLifecycleOwner) { state ->
            when (state) {
                is EditAccountViewModel.EditAccountState.NoChangeAccount -> {
                    noChangeAccount()
                }
                EditAccountViewModel.EditAccountState.EmailRegisterNotValidate -> {
                    emailRegisterNotValidate()
                }
                EditAccountViewModel.EditAccountState.ErrorLoadingAccount -> {
                    errorLoadingAccount()
                }
                EditAccountViewModel.EditAccountState.ErrorUpdateAccount -> {
                    errorUpdateAccount()
                }
                EditAccountViewModel.EditAccountState.Loading -> {
                    loading()
                }
                EditAccountViewModel.EditAccountState.LoginNotValidate -> {
                    loginNotValidate()
                }
                is EditAccountViewModel.EditAccountState.SetAccountDate -> {
                    setAccountDate(state.account)
                }
                is EditAccountViewModel.EditAccountState.SuccessUpdateAccount -> {
                    successUpdateAccount(state.userLogin)
                }
                is EditAccountViewModel.EditAccountState.EmailRegisterIsValidate -> {
                    emailRegisterIsValidate(state.text)
                }
                is EditAccountViewModel.EditAccountState.LoginIsValidate -> {
                    loginIsValidate(state.text)
                }
                is EditAccountViewModel.EditAccountState.PasswordIsValidate -> {
                    passwordIsValidate(state.text)
                }
                is EditAccountViewModel.EditAccountState.PasswordNotValidate -> {
                    passwordNotValidate()
                }
            }
        }
    }

    private fun initRxTextChangerValidation() {
        RxTextView.textChanges(binding.passwordChangeEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .subscribe({ text ->
                viewModel.passwordValidation(text)
            }, {
            })

        RxTextView.textChanges(binding.userNameChangeEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .subscribe({ text ->
                viewModel.loginValidation(text)
            }, {
            })

        RxTextView.textChanges(binding.emailChangeEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .subscribe({ text ->
                viewModel.emailRegisterValidation(text)
            }, {
            })
    }

    private fun checkNullSignUpData(): Boolean {
        return userLogin != "" && userPassword != "" && userEmail != ""
    }

    private fun setAccountDate(account: Account) {
        TransitionManager.beginDelayedTransition(binding.root)
        userLogin = account.userName
        userEmail = account.email
        userPassword = account.password
        binding.userNameChange.editText?.setText(account.userName)
        binding.emailChange.editText?.setText(account.email)
        binding.passwordChange.editText?.setText(account.password)
        binding.constrainEditInformation.show()
    }

    private fun loading() {
        binding.constrainEditInformation.hide()
    }

    private fun errorLoadingAccount() {
        binding.constrainEditInformation.show()
        binding.root.showSnackBarError(getString(R.string.errorLoadingAccount), "", {})
    }

    private fun passwordIsValidate(text: CharSequence?) {
        binding.passwordChange.error = null
        userPassword = text.toString()
        if (userLogin != "" && userEmail != "") {
            binding.saveChangeAccount.alpha = ALFA_LOGIN_BUTTON
        }
    }

    private fun passwordNotValidate() {
        userPassword = ""
        binding.passwordChange.error = getString(R.string.error_input_email) + "($PASSWORD_STR)"
        binding.saveChangeAccount.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    private fun loginIsValidate(text: CharSequence?) {
        binding.userNameChange.error = null
        userLogin = text.toString()
        if (userPassword != "" && userEmail != "") {
            binding.saveChangeAccount.alpha = ALFA_LOGIN_BUTTON
        }
    }

    private fun loginNotValidate() {
        binding.userNameChange.error = getString(R.string.error_input_email) + "($LOGIN_STR)"
        userLogin = ""
        binding.saveChangeAccount.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    private fun emailRegisterIsValidate(text: CharSequence?) {
        binding.emailChange.error = null
        userEmail = text.toString()
        if (userLogin != "" && userPassword != "") {
            binding.saveChangeAccount.alpha = ALFA_LOGIN_BUTTON
        }
    }

    private fun emailRegisterNotValidate() {
        userEmail = ""
        binding.emailChange.error = getString(R.string.error_input_email) + "($EMAIL_STR)"
        binding.saveChangeAccount.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    private fun successUpdateAccount(userLogin: String) {
        binding.root.showSnackBarError(getString(R.string.dataUpdated), "", {})
        viewModel.arrowBack()
        (requireActivity() as EventLogoutAccountScreen).bottomNavigationSetTitleCurrentAccount(
            userLogin)
    }

    private fun errorUpdateAccount() {
        binding.root.showSnackBarError(getString(R.string.unique_email_username), "", {})
    }

    private fun noChangeAccount() {
        binding.root.showSnackBarError(getString(R.string.noChangeAccount), "", {})
    }

    companion object {
        fun getInstance(accountID: Int) = EditAccountFragment().apply { this.accountId = accountID }
    }
}
