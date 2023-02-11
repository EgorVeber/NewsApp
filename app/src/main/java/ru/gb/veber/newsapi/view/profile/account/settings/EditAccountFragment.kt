package ru.gb.veber.newsapi.view.profile.account.settings

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding.widget.RxTextView
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.EditAccountFragmentBinding
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.presenter.EditAccountPresenter
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.activity.EventLogoutAccountScreen
import ru.gb.veber.newsapi.view.profile.authorization.AuthorizationFragment.Companion.ALFA_HALF_LOGIN_BUTTON
import ru.gb.veber.newsapi.view.profile.authorization.AuthorizationFragment.Companion.ALFA_LOGIN_BUTTON

class EditAccountFragment : MvpAppCompatFragment(), EditAccountView, BackPressedListener {

    private var _binding: EditAccountFragmentBinding? = null
    private val binding get() = _binding!!

    private val presenter: EditAccountPresenter by moxyPresenter {
        EditAccountPresenter().apply {
            App.instance.appComponent.inject(this)
        }
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
        arguments?.getInt(ACCOUNT_ID).also { accountID ->
            presenter.getAccountDataBase(accountID ?: ACCOUNT_ID_DEFAULT)
        }
        initialization()
    }

    private fun initialization() {

        binding.backAccountScreen.setOnClickListener {
            presenter.backAccountScreen()
        }

        binding.saveChangeAccount.setOnClickListener {
            if (checkNullSignUpData()) {
                presenter.checkSaveAccount(userLogin, userPassword, userEmail)
            }
        }
        rxTextChangerValidation()

        //Тестируем токен
    }

    private fun checkNullSignUpData(): Boolean {
        return userLogin != "" && userPassword != "" && userEmail != ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }


    override fun setAccountDate(account: Account) {
        TransitionManager.beginDelayedTransition(binding.root)
        userLogin = account.userName
        userEmail = account.email
        userPassword = account.password
        binding.userNameChange.editText?.setText(account.userName)
        binding.emailChange.editText?.setText(account.email)
        binding.passwordChange.editText?.setText(account.password)
        binding.constrainEditInformation.show()
    }

    override fun loading() {
        binding.constrainEditInformation.hide()
    }

    override fun errorLoadingAccount() {
        binding.constrainEditInformation.show()
        binding.root.showSnackBarError(getString(R.string.errorLoadingAccount), "", {})
    }

    override fun passwordIsValidate(it: CharSequence?) {
        binding.passwordChange.error = null
        userPassword = it.toString()
        if (userLogin != "" && userEmail != "") {
            binding.saveChangeAccount.alpha = ALFA_LOGIN_BUTTON
        }
    }

    override fun passwordNotValidate(it: CharSequence?) {
        userPassword = ""
        binding.passwordChange.error = getString(R.string.errorInputEmail) + "($PASSWORD_STR)"
        binding.saveChangeAccount.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    override fun loginIsValidate(it: CharSequence?) {
        binding.userNameChange.error = null
        userLogin = it.toString()
        if (userPassword != "" && userEmail != "") {
            binding.saveChangeAccount.alpha = ALFA_LOGIN_BUTTON
        }
    }

    override fun loginNotValidate() {
        binding.userNameChange.error = getString(R.string.errorInputEmail) + "($LOGIN_STR)"
        userLogin = ""
        binding.saveChangeAccount.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    override fun emailRegisterIsValidate(it: CharSequence?) {
        binding.emailChange.error = null
        userEmail = it.toString()
        if (userLogin != "" && userPassword != "") {
            binding.saveChangeAccount.alpha = ALFA_LOGIN_BUTTON
        }
    }

    override fun emailRegisterNotValidate() {
        userEmail = ""
        binding.emailChange.error = getString(R.string.errorInputEmail) + "($EMAIL_STR)"
        binding.saveChangeAccount.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    override fun successUpdateAccount(userLogin: String) {
        binding.root.showSnackBarError(getString(R.string.dataUpdated), "", {})
        presenter.backAccountScreen()
        (requireActivity() as EventLogoutAccountScreen).bottomNavigationSetTitleCurrentAccount(
            userLogin)
    }

    override fun errorUpdateAccount() {
        binding.root.showSnackBarError(getString(R.string.uniqueEmailUsername), "", {})
    }

    override fun noChangeAccount() {
        binding.root.showSnackBarError(getString(R.string.noChangeAccount), "", {})
    }

    private fun rxTextChangerValidation() {
        RxTextView.textChanges(binding.passwordChangeEditText)
            .filter { it.toString().isNotEmpty() }
            .subscribe({
                presenter.passwordValidation(it)
            }, {
            })

        RxTextView.textChanges(binding.userNameChangeEditText)
            .filter { it.toString().isNotEmpty() }
            .subscribe({
                presenter.loginValidation(it)
            }, {
            })

        RxTextView.textChanges(binding.emailChangeEditText)
            .filter { it.toString().isNotEmpty() }
            .subscribe({
                presenter.emailRegisterValidation(it)
            }, {
            })
    }

    companion object {
        fun getInstance(bundle: Bundle) = EditAccountFragment().apply { arguments = bundle }
    }

}