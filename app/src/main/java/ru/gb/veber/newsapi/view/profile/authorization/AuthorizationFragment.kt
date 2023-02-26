package ru.gb.veber.newsapi.view.profile.authorization

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.jakewharton.rxbinding.widget.RxTextView
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.AuthorizationFragmentBinding
import ru.gb.veber.newsapi.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.ColorUtils.getColor
import ru.gb.veber.newsapi.utils.extentions.EMAIL_STR
import ru.gb.veber.newsapi.utils.extentions.LOGIN_STR
import ru.gb.veber.newsapi.utils.extentions.PASSWORD_STR
import ru.gb.veber.newsapi.utils.extentions.showSnackBarError
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.activity.EventLogoutAccountScreen
import ru.gb.veber.newsapi.view.activity.OpenScreen
import javax.inject.Inject

class AuthorizationFragment : Fragment(), BackPressedListener {

    private var _binding: AuthorizationFragmentBinding? = null
    private val binding get() = _binding!!

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

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val authorizationViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[AuthorizationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = AuthorizationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.instance.appComponent.inject(this)
        val accountId = arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT
        init()
        initViewModel(accountId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return authorizationViewModel.onBackPressedRouter()
    }

    private fun init() {

        setSpanRegulationsTv()
        constraintSetLogin.clone(binding.root)
        rxTextChangerValidation()

        binding.privacyPolicy.setOnClickListener {
            authorizationViewModel.openScreenWebView(getString(R.string.team_site))
        }

        binding.backMainScreen.setOnClickListener {
            authorizationViewModel.openMain()
        }

        binding.changeRegisterButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.root, changeBounds)
            authorizationViewModel.changeRegisterAnim()
            if (checkNullRegisterData()) {
                binding.registerButton.alpha = ALFA_LOGIN_BUTTON
            }
        }

        binding.changeSignButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.root, changeBounds)
            authorizationViewModel.changeLoginAnim()
            if (checkNullSignUpData()) {
                binding.signInButton.alpha = ALFA_LOGIN_BUTTON
            }
        }

        binding.signInButton.setOnClickListener {
            if (checkNullSignUpData()) {
                authorizationViewModel.checkSignIn(userLogin, userPassword)
            }
        }

        binding.registerButton.setOnClickListener {
            if (checkNullRegisterData()) {
                authorizationViewModel.createAccount(
                    userRegisterLogin,
                    userEmail,
                    userRegisterPassword
                )
            }
        }
    }

    private fun initViewModel(accountId: Int) {
        authorizationViewModel.subscribe(accountId).observe(viewLifecycleOwner) { state ->
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
                is AuthorizationViewModel.AuthorizationViewState.SuccessRegister -> {
                    successRegister(state.id)
                }
                is AuthorizationViewModel.AuthorizationViewState.SuccessSignIn -> {
                    successSignIn(state.id)
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
                authorizationViewModel.passwordValidation(text)
            }, {
            })

        RxTextView.textChanges(binding.userNameEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .skip(4)
            .subscribe({ text ->
                authorizationViewModel.loginValidation(text)
            }, {
            })

        RxTextView.textChanges(binding.passwordRegisterEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .skip(6)
            .subscribe({ text ->
                authorizationViewModel.passwordRegisterValidation(text)
            }, {
            })

        RxTextView.textChanges(binding.userNameRegisterEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .skip(4)
            .subscribe({ text ->
                authorizationViewModel.loginRegisterValidation(text)
            }, {
            })

        //TODO NewsAndroid-4 Почистить проект от хлама
        var subscription: rx.Subscription? = RxTextView.textChanges(binding.emailRegisterEditText)
            .filter { text -> text.toString().isNotEmpty() }
            .skip(5)
            .subscribe({ text ->
                authorizationViewModel.emailRegisterValidation(text)
            }, {
            })
    }

    private fun errorSignIn() {
        binding.passwordTextInput.error = getString(R.string.invalid_password)
    }

    private fun errorRegister() {
        binding.userNameRegisterTextInput.error = getString(R.string.check_login)
        binding.emailRegisterTextInput.error = getString(R.string.check_email)
        binding.root.showSnackBarError(getString(R.string.unique_email_username), "", {})
    }

    private fun successSignIn(id: Int) {
        authorizationViewModel.openScreenProfile(id)
    }

    private fun successRegister(id: Int) {
        binding.root.showSnackBarError(getString(R.string.create_account), "", {})
        authorizationViewModel.openScreenProfile(id)
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
