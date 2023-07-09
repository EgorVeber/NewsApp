package ru.gb.veber.newsapi.presentation.profile.account.settings

import android.transition.TransitionManager
import com.jakewharton.rxbinding.widget.RxTextView
import ru.gb.veber.newsapi.common.UiCoreStrings
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.domain.models.AccountModel
import ru.gb.veber.newsapi.presentation.activity.callbackhell.EventLogoutAccountScreen
import ru.gb.veber.newsapi.presentation.base.NewsFragment
import ru.gb.veber.newsapi.presentation.profile.authorization.AuthorizationFragment.Companion.ALFA_HALF_LOGIN_BUTTON
import ru.gb.veber.newsapi.presentation.profile.authorization.AuthorizationFragment.Companion.ALFA_LOGIN_BUTTON
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import ru.gb.veber.ui_common.BUNDLE_ACCOUNT_ID_KEY
import ru.gb.veber.ui_common.hide
import ru.gb.veber.ui_common.show
import ru.gb.veber.ui_common.utils.AuthPattern.EMAIL_EXAMPLE
import ru.gb.veber.ui_common.utils.AuthPattern.LOGIN_EXAMPLE
import ru.gb.veber.ui_common.utils.AuthPattern.PASSWORD_EXAMPLE
import ru.gb.veber.ui_common.utils.BundleInt
import ru.gb.veber.ui_core.databinding.EditAccountFragmentBinding
import ru.gb.veber.ui_core.extentions.showSnackBar

class EditAccountFragment :
    NewsFragment<EditAccountFragmentBinding, EditAccountViewModel>(EditAccountFragmentBinding::inflate) {

    private var userLogin: String = ""
    private var userPassword: String = ""
    private var userEmail: String = ""

    private var accountId by BundleInt(BUNDLE_ACCOUNT_ID_KEY, ACCOUNT_ID_DEFAULT)

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
                    setAccountDate(state.accountModel)
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

    private fun setAccountDate(accountModel: AccountModel) {
        TransitionManager.beginDelayedTransition(binding.root)
        userLogin = accountModel.userName
        userEmail = accountModel.email
        userPassword = accountModel.password
        binding.userNameChange.editText?.setText(accountModel.userName)
        binding.emailChange.editText?.setText(accountModel.email)
        binding.passwordChange.editText?.setText(accountModel.password)
        binding.constrainEditInformation.show()
    }

    private fun loading() {
        binding.constrainEditInformation.hide()
    }

    private fun errorLoadingAccount() {
        binding.constrainEditInformation.show()
        this.showSnackBar(getString(UiCoreStrings.errorLoadingAccount))
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
        binding.passwordChange.error = getString(UiCoreStrings.error_input_email) + "($PASSWORD_EXAMPLE)"
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
        binding.userNameChange.error = getString(UiCoreStrings.error_input_email) + "($LOGIN_EXAMPLE)"
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
        binding.emailChange.error = getString(UiCoreStrings.error_input_email) + "($EMAIL_EXAMPLE)"
        binding.saveChangeAccount.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    private fun successUpdateAccount(userLogin: String) {
        this.showSnackBar(getString(UiCoreStrings.dataUpdated))
        viewModel.arrowBack()
        (requireActivity() as EventLogoutAccountScreen).bottomNavigationSetTitleCurrentAccount(
            userLogin)
    }

    private fun errorUpdateAccount() {
        this.showSnackBar(getString(UiCoreStrings.unique_email_username))
    }

    private fun noChangeAccount() {
        this.showSnackBar(getString(UiCoreStrings.noChangeAccount))
    }

    companion object {
        fun getInstance(accountID: Int) = EditAccountFragment().apply { this.accountId = accountID }
    }
}
