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
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.jakewharton.rxbinding.widget.RxTextView
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.AuthorizationFragmentBinding
import ru.gb.veber.newsapi.presenter.AuthorizationPresenter
import ru.gb.veber.newsapi.utils.ColorUtils.getColor
import ru.gb.veber.newsapi.utils.EMAIL_STR
import ru.gb.veber.newsapi.utils.LOGIN_STR
import ru.gb.veber.newsapi.utils.PASSWORD_STR
import ru.gb.veber.newsapi.utils.showSnackBarError
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.activity.EventLogoutAccountScreen
import ru.gb.veber.newsapi.view.activity.OpenScreen


class AuthorizationFragment : MvpAppCompatFragment(), AuthorizationView,
    BackPressedListener {

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

    private val presenter: AuthorizationPresenter by moxyPresenter {
        AuthorizationPresenter().apply {
            App.instance.appComponent.inject(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = AuthorizationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun init() {

        setSpanRegulationsTv()
        constraintSetLogin.clone(binding.root)
        rxTextChangerValidation()

        binding.privacyPolicy.setOnClickListener {
            presenter.openScreenWebView(getString(R.string.team_site))
        }

        binding.backMainScreen.setOnClickListener {
            presenter.openMain()
        }

        binding.changeRegisterButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.root, changeBounds)
            presenter.changeRegisterAnim()
            if (checkNullRegisterData()) {
                binding.registerButton.alpha = ALFA_LOGIN_BUTTON
            }
        }

        binding.changeSignButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.root, changeBounds)
            presenter.changeLoginAnim()
            if (checkNullSignUpData()) {
                binding.signInButton.alpha = ALFA_LOGIN_BUTTON
            }
        }

        binding.signInButton.setOnClickListener {
            if (checkNullSignUpData()) {
                presenter.checkSignIn(userLogin, userPassword)
            }
        }

        binding.registerButton.setOnClickListener {
            if (checkNullRegisterData()) {
                presenter.createAccount(userRegisterLogin, userEmail, userRegisterPassword)
            }
        }
    }

    private fun rxTextChangerValidation() {

        RxTextView.textChanges(binding.passwordEditText)
            .filter { it.toString().isNotEmpty() }
            .skip(6)
            .subscribe({
                presenter.passwordValidation(it)
            }, {
            })

        RxTextView.textChanges(binding.userNameEditText)
            .filter { it.toString().isNotEmpty() }
            .skip(4)
            .subscribe({
                presenter.loginValidation(it)
            }, {
            })

        RxTextView.textChanges(binding.passwordRegisterEditText)
            .filter { it.toString().isNotEmpty() }
            .skip(6)
            .subscribe({
                presenter.passwordRegisterValidation(it)
            }, {
            })

        RxTextView.textChanges(binding.userNameRegisterEditText)
            .filter { it.toString().isNotEmpty() }
            .skip(4)
            .subscribe({
                presenter.loginRegisterValidation(it)
            }, {
            })

       //TODO NewsAndroid-4 Почистить проект от хлама
       var subscription: rx.Subscription? =  RxTextView.textChanges(binding.emailRegisterEditText)
            .filter { it.toString().isNotEmpty() }
            .skip(5)
            .subscribe({
                presenter.emailRegisterValidation(it)
            }, {
            })


    }


    override fun errorSignIn() {
        binding.passwordTextInput.error = getString(R.string.invalid_password)
    }

    override fun errorRegister() {
        binding.userNameRegisterTextInput.error = getString(R.string.check_login)
        binding.emailRegisterTextInput.error = getString(R.string.check_email)
        binding.root.showSnackBarError(getString(R.string.unique_email_username), "", {})
    }

    override fun successSignIn(id: Int) {
        presenter.openScreenProfile(id)
    }

    override fun successRegister(id: Int) {
        binding.root.showSnackBarError(getString(R.string.create_account), "", {})
        presenter.openScreenProfile(id)
    }


    override fun sendActivityOpenScreen() {
        (requireActivity() as OpenScreen).openMainScreen()
    }

    override fun emptyAccount() {
        binding.userNameTextInput.error = getString(R.string.user_none)
    }

    override fun setBottomNavigationIcon(checkLogin: String) {
        (requireActivity() as EventLogoutAccountScreen).bottomNavigationSetCurrentAccount(checkLogin)
    }


    override fun loginIsValidate(charSequence: CharSequence?) {
        binding.userNameTextInput.error = null
        userLogin = charSequence.toString()
        if (userPassword != "") {
            binding.signInButton.alpha = ALFA_LOGIN_BUTTON
        }
    }

    override fun loginNotValidate() {
        binding.userNameTextInput.error = getString(R.string.error_input_email) + "($LOGIN_STR)"
        userLogin = ""
        binding.signInButton.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    override fun passwordIsValidate(it: CharSequence?) {
        binding.passwordTextInput.error = null
        userPassword = it.toString()
        if (userLogin != "") {
            binding.signInButton.alpha = ALFA_LOGIN_BUTTON
        }
    }

    override fun passwordNotValidate(it: CharSequence?) {
        userPassword = ""
        binding.passwordTextInput.error =
            getString(R.string.error_input_email) + "($PASSWORD_STR)"
        binding.signInButton.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    override fun loginRegisterIsValidate(it: CharSequence?) {
        binding.userNameRegisterTextInput.error = null
        userRegisterLogin = it.toString()
        if (userRegisterPassword != "" && userEmail != "") {
            binding.registerButton.alpha = ALFA_LOGIN_BUTTON
        }
    }

    override fun loginRegisterNotValidate() {
        binding.userNameRegisterTextInput.error =
            getString(R.string.error_input_email) + "($LOGIN_STR)"
        userRegisterLogin = ""
        binding.registerButton.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    override fun passwordRegisterIsValidate(it: CharSequence?) {
        binding.passwordRegisterTextInput.error = null
        userRegisterPassword = it.toString()
        if (userRegisterLogin != "" && userEmail != "") {
            binding.registerButton.alpha = ALFA_LOGIN_BUTTON
        }
    }

    override fun passwordRegisterNotValidate(it: CharSequence?) {
        userRegisterPassword = ""
        binding.passwordRegisterTextInput.error =
            getString(R.string.error_input_email) + "($PASSWORD_STR)"
        binding.registerButton.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    override fun emailRegisterIsValidate(it: CharSequence?) {
        binding.emailRegisterTextInput.error = null
        userEmail = it.toString()
        if (userRegisterLogin != "" && userRegisterPassword != "") {
            binding.registerButton.alpha = ALFA_LOGIN_BUTTON
        }
    }

    override fun emailRegisterNotValidate() {
        userEmail = ""
        binding.emailRegisterTextInput.error =
            getString(R.string.error_input_email) + "($EMAIL_STR)"
        binding.registerButton.alpha = ALFA_HALF_LOGIN_BUTTON
    }

    override fun setRegisterAnim() {
        constraintSetLogin.clear(R.id.userNameTextInput, ConstraintSet.END)
        constraintSetLogin.clear(R.id.userNameTextInput, ConstraintSet.START)
        constraintSetLogin.connect(R.id.userNameTextInput,
            ConstraintSet.START,
            R.id.constraintLayoutSet,
            ConstraintSet.END)
        constraintSetLogin.applyTo(binding.root)

        constraintSetLogin.connect(R.id.userNameRegisterTextInput,
            ConstraintSet.START,
            R.id.constraintLayoutSet,
            ConstraintSet.START)
        constraintSetLogin.connect(R.id.userNameRegisterTextInput,
            ConstraintSet.END,
            R.id.constraintLayoutSet,
            ConstraintSet.END)
        constraintSetLogin.applyTo(binding.root)

        constraintSetLogin.clear(R.id.privacyPolicy, ConstraintSet.TOP)
        constraintSetLogin.connect(R.id.privacyPolicy,
            ConstraintSet.TOP,
            R.id.changeSignButton,
            ConstraintSet.BOTTOM)
        constraintSetLogin.applyTo(binding.root)
    }

    override fun setLoginAnim() {
        constraintSetLogin.clear(R.id.userNameRegisterTextInput, ConstraintSet.END)
        constraintSetLogin.clear(R.id.userNameRegisterTextInput, ConstraintSet.START)
        constraintSetLogin.connect(R.id.userNameRegisterTextInput,
            ConstraintSet.END,
            R.id.constraintLayoutSet,
            ConstraintSet.START)
        constraintSetLogin.applyTo(binding.root)

        constraintSetLogin.clear(R.id.userNameTextInput, ConstraintSet.START)
        constraintSetLogin.connect(R.id.userNameTextInput,
            ConstraintSet.START,
            R.id.constraintLayoutSet,
            ConstraintSet.START)
        constraintSetLogin.connect(R.id.userNameTextInput,
            ConstraintSet.END,
            R.id.constraintLayoutSet,
            ConstraintSet.END)
        constraintSetLogin.applyTo(binding.root)
        constraintSetLogin.clear(R.id.privacyPolicy, ConstraintSet.TOP)
        constraintSetLogin.connect(R.id.privacyPolicy, ConstraintSet.TOP,
            R.id.changeRegisterButton,
            ConstraintSet.BOTTOM)
        constraintSetLogin.applyTo(binding.root)
    }

    private fun setSpanRegulationsTv() {

        SpannableStringBuilder(binding.privacyPolicy.text).also {span->
            var colorPrimary = this.getColor(R.color.color_primary_app)

            span.setSpan(ForegroundColorSpan(colorPrimary),
                SPAN_START_INDEX_PRIVACY,
                SPAN_START_END_PRIVACY,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            span.setSpan(ForegroundColorSpan(colorPrimary),
                SPAN_START_START_POLICY,
                SPAN_START_END_POLICY,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            span.setSpan(UnderlineSpan(),
                SPAN_START_INDEX_PRIVACY,
                SPAN_START_END_PRIVACY,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            span.setSpan(UnderlineSpan(),
                SPAN_START_START_POLICY,
                SPAN_START_END_POLICY,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    companion object {
        const val DURATION_REGISTER = 650L
        const val ALFA_LOGIN_BUTTON = 1F
        const val ALFA_HALF_LOGIN_BUTTON = 0.5F
        const val SPAN_START_INDEX_PRIVACY = 41
        const val SPAN_START_END_PRIVACY = 55
        const val SPAN_START_START_POLICY= 60
        const val SPAN_START_END_POLICY = 76
        fun getInstance(): AuthorizationFragment {
            return AuthorizationFragment()
        }
    }
}
