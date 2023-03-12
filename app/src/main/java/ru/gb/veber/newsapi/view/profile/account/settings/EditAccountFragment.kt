package ru.gb.veber.newsapi.view.profile.account.settings

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding.widget.RxTextView
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.EditAccountFragmentBinding
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.core.utils.*
import ru.gb.veber.newsapi.core.utils.extentions.EMAIL_STR
import ru.gb.veber.newsapi.core.utils.extentions.LOGIN_STR
import ru.gb.veber.newsapi.core.utils.extentions.PASSWORD_STR
import ru.gb.veber.newsapi.core.utils.extentions.hide
import ru.gb.veber.newsapi.core.utils.extentions.show
import ru.gb.veber.newsapi.core.utils.extentions.showSnackBarError
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.activity.EventLogoutAccountScreen
import ru.gb.veber.newsapi.view.profile.authorization.AuthorizationFragment.Companion.ALFA_HALF_LOGIN_BUTTON
import ru.gb.veber.newsapi.view.profile.authorization.AuthorizationFragment.Companion.ALFA_LOGIN_BUTTON
import javax.inject.Inject

class EditAccountFragment : Fragment(), BackPressedListener {

    private var _binding: EditAccountFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val editAccountViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[EditAccountViewModel::class.java]
    }

    private var userLogin: String = ""
    private var userPassword: String = ""
    private var userEmail: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = EditAccountFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.instance.appComponent.inject(this)
        initialization()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return editAccountViewModel.onBackPressedRouter()
    }

    private fun initialization() {
        val accountId = arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT
        initViewModel(accountId)
        initView()
        initRxTextChangerValidation()
    }

    private fun initViewModel(accountId: Int) {
        editAccountViewModel.subscribe(accountId).observe(viewLifecycleOwner) { state ->
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

    private fun initView() {
        binding.backAccountScreen.setOnClickListener {
            editAccountViewModel.arrowBack()
        }

        binding.saveChangeAccount.setOnClickListener {
            if (checkNullSignUpData()) {
                editAccountViewModel.checkSaveAccount(
                    userLogin = userLogin,
                    userPassword = userPassword,
                    userEmail = userEmail
                )
            }
        }
    }

    private fun initRxTextChangerValidation() {
        RxTextView.textChanges(binding.passwordChangeEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .subscribe({ text ->
                editAccountViewModel.passwordValidation(text)
            }, {
            })

        RxTextView.textChanges(binding.userNameChangeEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .subscribe({ text ->
                editAccountViewModel.loginValidation(text)
            }, {
            })

        RxTextView.textChanges(binding.emailChangeEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .subscribe({ text ->
                editAccountViewModel.emailRegisterValidation(text)
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
        editAccountViewModel.arrowBack()
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
        fun getInstance(bundle: Bundle) = EditAccountFragment().apply { arguments = bundle }
    }
}
