package ru.gb.veber.newsapi.presentation.profile.authorization

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.animation.AnticipateOvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.jakewharton.rxbinding.widget.RxTextView
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.base.NewsFragment
import ru.gb.veber.newsapi.common.extentions.EMAIL_STR
import ru.gb.veber.newsapi.common.extentions.LOGIN_STR
import ru.gb.veber.newsapi.common.extentions.PASSWORD_STR
import ru.gb.veber.newsapi.common.extentions.observeFlow
import ru.gb.veber.newsapi.common.extentions.showSnackBar
import ru.gb.veber.newsapi.common.utils.ColorUtils.getColor
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.AuthorizationFragmentBinding
import ru.gb.veber.newsapi.presentation.activity.EventLogoutAccountScreen
import ru.gb.veber.newsapi.presentation.activity.OpenScreen

class AuthorizationFragment : NewsFragment<AuthorizationFragmentBinding, AuthorizationViewModel>(
    AuthorizationFragmentBinding::inflate
) {
    private val constraintSetLogin = ConstraintSet()
    private val changeBounds = ChangeBounds().apply {
        duration = DURATION_REGISTER
        interpolator = AnticipateOvershootInterpolator(ALFA_LOGIN_BUTTON)
    }

    private var userLogin: String = ""
    private var userPassword: String = ""
    private var userRegisterLogin: String = ""
    private var userRegisterPassword: String = ""
    private var userEmail: String = ""

    override fun getViewModelClass(): Class<AuthorizationViewModel> {
        return AuthorizationViewModel::class.java
    }

    override fun onInject() {
        App.instance.appComponent.inject(this)
    }

    override fun onInitView() {

        setSpanRegulationsTv()
        constraintSetLogin.clone(binding.root)
        rxTextChangerValidation()

        binding.privacyPolicy.setOnClickListener {
            viewModel.openScreenWebView(getString(R.string.team_site))
        }

        binding.backMainScreen.setOnClickListener {
            viewModel.openMain()
        }

        binding.changeRegisterButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.root, changeBounds)
            viewModel.changeRegisterAnim()
            if (checkNullRegisterData()) {
                binding.registerButton.alpha = ALFA_LOGIN_BUTTON
            }
        }

        binding.changeSignButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.root, changeBounds)
            viewModel.changeLoginAnim()
            if (checkNullSignUpData()) {
                binding.signInButton.alpha = ALFA_LOGIN_BUTTON
            }
        }

        binding.signInButton.setOnClickListener {
            if (checkNullSignUpData()) {
                viewModel.checkSignIn(userLogin, userPassword)
            }
        }

        binding.registerButton.setOnClickListener {
            if (checkNullRegisterData()) {
                viewModel.createAccount(
                    userRegisterLogin,
                    userEmail,
                    userRegisterPassword
                )
            }
        }
    }

    override fun onObserveData() {
        viewModel.logInFlow.observeFlow(this) { loginInfo ->
            if (loginInfo.first) successRegister(loginInfo.second)
        }

        viewModel.signInFlow.observeFlow(this) { accountId ->
            successSignIn(accountId)
        }

        viewModel.subscribe().observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthorizationViewModel.AuthorizationViewState.EmailRegisterIsValidate -> {
                    emailRegisterIsValidate(state.email)
                }
                is AuthorizationViewModel.AuthorizationViewState.LoginIsValidate -> {
                    loginIsValidate(state.charSequence)
                }
                is AuthorizationViewModel.AuthorizationViewState.LoginRegisterIsValidate -> {
                    loginRegisterIsValidate(state.login)
                }
                is AuthorizationViewModel.AuthorizationViewState.PasswordIsValidate -> {
                    passwordIsValidate(state.password)
                }
                is AuthorizationViewModel.AuthorizationViewState.PasswordNotValidate -> {
                    passwordNotValidate(state.password)
                }
                is AuthorizationViewModel.AuthorizationViewState.PasswordRegisterIsValidate -> {
                    passwordRegisterIsValidate(state.password)
                }
                is AuthorizationViewModel.AuthorizationViewState.PasswordRegisterNotValidate -> {
                    passwordRegisterNotValidate(state.password)
                }
                is AuthorizationViewModel.AuthorizationViewState.SetBottomNavigationIcon -> {
                    setBottomNavigationIcon(state.checkLogin)
                }
                AuthorizationViewModel.AuthorizationViewState.EmailRegisterNotValidate -> {
                    emailRegisterNotValidate()
                }
                AuthorizationViewModel.AuthorizationViewState.EmptyAccount -> {
                    emptyAccount()
                }
                AuthorizationViewModel.AuthorizationViewState.ErrorRegister -> {
                    errorRegister()
                }
                AuthorizationViewModel.AuthorizationViewState.ErrorSignIn -> {
                    errorSignIn()
                }

                AuthorizationViewModel.AuthorizationViewState.LoginNotValidate -> {
                    loginNotValidate()
                }

                AuthorizationViewModel.AuthorizationViewState.LoginRegisterNotValidate -> {
                    loginRegisterNotValidate()
                }

                AuthorizationViewModel.AuthorizationViewState.SendActivityOpenScreen -> {
                    sendActivityOpenScreen()
                }

                AuthorizationViewModel.AuthorizationViewState.SetLoginAnim -> {
                    setLoginAnim()
                }
                AuthorizationViewModel.AuthorizationViewState.SetRegisterAnim -> {
                    setRegisterAnim()
                }
            }
        }
    }

    private fun rxTextChangerValidation() {

        RxTextView.textChanges(binding.passwordEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .skip(6)
            .subscribe({ text ->
                viewModel.passwordValidation(text)
            }, {
            })

        RxTextView.textChanges(binding.userNameEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .skip(4)
            .subscribe({ text ->
                viewModel.loginValidation(text)
            }, {
            })

        RxTextView.textChanges(binding.passwordRegisterEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .skip(6)
            .subscribe({ text ->
                viewModel.passwordRegisterValidation(text)
            }, {
            })

        RxTextView.textChanges(binding.userNameRegisterEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .skip(4)
            .subscribe({ text ->
                viewModel.loginRegisterValidation(text)
            }, {
            })

        RxTextView.textChanges(binding.emailRegisterEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .skip(5)
            .subscribe({ text ->
                viewModel.emailRegisterValidation(text)
            }, {
            })
    }

    private fun errorSignIn() {
        binding.passwordTextInput.error = getString(R.string.invalid_password)
    }

    private fun errorRegister() {
        binding.userNameRegisterTextInput.error = getString(R.string.check_login)
        binding.emailRegisterTextInput.error = getString(R.string.check_email)
        this.showSnackBar(getString(R.string.unique_email_username))
    }

    private fun successSignIn(id: Int) {
        viewModel.openScreenProfile(id)
    }

    private fun successRegister(id: Int) {
        this.showSnackBar(getString(R.string.create_account))
        viewModel.openScreenProfile(id)
    }

    private fun sendActivityOpenScreen() {
        (requireActivity() as OpenScreen).openMainScreen()
    }

    private fun emptyAccount() {
        binding.userNameTextInput.error = getString(R.string.user_none)
    }

    private fun setBottomNavigationIcon(checkLogin: String) {
        (requireActivity() as EventLogoutAccountScreen).bottomNavigationSetCurrentAccount(checkLogin)
    }

    private fun loginIsValidate(charSequence: CharSequence?) {
        binding.userNameTextInput.error = null
        userLogin = charSequence.toString()
        if (userPassword != "") {
            binding.signInButton.alpha = ALFA_LOGIN_BUTTON
        }
    }

    private fun loginNotValidate() {
        binding.userNameTextInput.error = getString(R.string.error_input_email) + "($LOGIN_STR)"
        userLogin = ""
        binding.signInButton.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    private fun passwordIsValidate(password: CharSequence?) {
        binding.passwordTextInput.error = null
        userPassword = password.toString()
        if (userLogin != "") {
            binding.signInButton.alpha = ALFA_LOGIN_BUTTON
        }
    }

    private fun passwordNotValidate(password: CharSequence?) {
        userPassword = ""
        binding.passwordTextInput.error =
            getString(R.string.error_input_email) + "($PASSWORD_STR)"
        binding.signInButton.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    private fun loginRegisterIsValidate(login: CharSequence?) {
        binding.userNameRegisterTextInput.error = null
        userRegisterLogin = login.toString()
        if (userRegisterPassword != "" && userEmail != "") {
            binding.registerButton.alpha = ALFA_LOGIN_BUTTON
        }
    }

    private fun loginRegisterNotValidate() {
        binding.userNameRegisterTextInput.error =
            getString(R.string.error_input_email) + "($LOGIN_STR)"
        userRegisterLogin = ""
        binding.registerButton.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    private fun passwordRegisterIsValidate(password: CharSequence?) {
        binding.passwordRegisterTextInput.error = null
        userRegisterPassword = password.toString()
        if (userRegisterLogin != "" && userEmail != "") {
            binding.registerButton.alpha = ALFA_LOGIN_BUTTON
        }
    }

    private fun passwordRegisterNotValidate(password: CharSequence?) {
        userRegisterPassword = ""
        binding.passwordRegisterTextInput.error =
            getString(R.string.error_input_email) + "($PASSWORD_STR)"
        binding.registerButton.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    private fun emailRegisterIsValidate(email: CharSequence?) {
        binding.emailRegisterTextInput.error = null
        userEmail = email.toString()
        if (userRegisterLogin != "" && userRegisterPassword != "") {
            binding.registerButton.alpha = ALFA_LOGIN_BUTTON
        }
    }

    private fun emailRegisterNotValidate() {
        userEmail = ""
        binding.emailRegisterTextInput.error =
            getString(R.string.error_input_email) + "($EMAIL_STR)"
        binding.registerButton.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    private fun setRegisterAnim() {
        constraintSetLogin.clear(R.id.userNameTextInput, ConstraintSet.END)
        constraintSetLogin.clear(R.id.userNameTextInput, ConstraintSet.START)
        constraintSetLogin.connect(
            R.id.userNameTextInput,
            ConstraintSet.START,
            R.id.constraintLayoutSet,
            ConstraintSet.END
        )
        constraintSetLogin.applyTo(binding.root)

        constraintSetLogin.connect(
            R.id.userNameRegisterTextInput,
            ConstraintSet.START,
            R.id.constraintLayoutSet,
            ConstraintSet.START
        )
        constraintSetLogin.connect(
            R.id.userNameRegisterTextInput,
            ConstraintSet.END,
            R.id.constraintLayoutSet,
            ConstraintSet.END
        )
        constraintSetLogin.applyTo(binding.root)

        constraintSetLogin.clear(R.id.privacyPolicy, ConstraintSet.TOP)
        constraintSetLogin.connect(
            R.id.privacyPolicy,
            ConstraintSet.TOP,
            R.id.changeSignButton,
            ConstraintSet.BOTTOM
        )
        constraintSetLogin.applyTo(binding.root)
    }

    private fun setLoginAnim() {
        constraintSetLogin.clear(R.id.userNameRegisterTextInput, ConstraintSet.END)
        constraintSetLogin.clear(R.id.userNameRegisterTextInput, ConstraintSet.START)
        constraintSetLogin.connect(
            R.id.userNameRegisterTextInput,
            ConstraintSet.END,
            R.id.constraintLayoutSet,
            ConstraintSet.START
        )
        constraintSetLogin.applyTo(binding.root)

        constraintSetLogin.clear(R.id.userNameTextInput, ConstraintSet.START)
        constraintSetLogin.connect(
            R.id.userNameTextInput,
            ConstraintSet.START,
            R.id.constraintLayoutSet,
            ConstraintSet.START
        )
        constraintSetLogin.connect(
            R.id.userNameTextInput,
            ConstraintSet.END,
            R.id.constraintLayoutSet,
            ConstraintSet.END
        )
        constraintSetLogin.applyTo(binding.root)
        constraintSetLogin.clear(R.id.privacyPolicy, ConstraintSet.TOP)
        constraintSetLogin.connect(
            R.id.privacyPolicy, ConstraintSet.TOP,
            R.id.changeRegisterButton,
            ConstraintSet.BOTTOM
        )
        constraintSetLogin.applyTo(binding.root)
    }

    private fun setSpanRegulationsTv() {

        SpannableStringBuilder(binding.privacyPolicy.text).also { span ->
            var colorPrimary = this.getColor(R.color.color_primary_app)

            span.setSpan(
                ForegroundColorSpan(colorPrimary),
                SPAN_START_INDEX_PRIVACY,
                SPAN_START_END_PRIVACY,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            span.setSpan(
                ForegroundColorSpan(colorPrimary),
                SPAN_START_START_POLICY,
                SPAN_START_END_POLICY,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            span.setSpan(
                UnderlineSpan(),
                SPAN_START_INDEX_PRIVACY,
                SPAN_START_END_PRIVACY,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            span.setSpan(
                UnderlineSpan(),
                SPAN_START_START_POLICY,
                SPAN_START_END_POLICY,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            binding.privacyPolicy.text = span
            span.removeSpan(this)
        }
    }

    private fun checkNullSignUpData(): Boolean {
        return userLogin != "" && userPassword != ""
    }

    private fun checkNullRegisterData(): Boolean {
        return userRegisterLogin != "" && userRegisterPassword != "" && userEmail != ""
    }

    companion object {
        const val DURATION_REGISTER = 650L
        const val ALFA_LOGIN_BUTTON = 1F
        const val ALFA_HALF_LOGIN_BUTTON = 0.5F
        const val SPAN_START_INDEX_PRIVACY = 41
        const val SPAN_START_END_PRIVACY = 55
        const val SPAN_START_START_POLICY = 60
        const val SPAN_START_END_POLICY = 76
        fun getInstance(): AuthorizationFragment {
            return AuthorizationFragment()

        }
    }
}
