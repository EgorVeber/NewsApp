package ru.gb.veber.newsapi.view.profile.authorization

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.jakewharton.rxbinding.widget.RxTextView
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.AuthorizationFragmentBinding
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.presenter.AuthorizationPresenter
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
        interpolator = AnticipateOvershootInterpolator(1F)
    }

    private var userLogin: String = ""
    private var userPassword: String = ""
    private var userRegisterLogin: String = ""
    private var userRegisterPassword: String = ""
    private var userEmail: String = ""

    private val presenter: AuthorizationPresenter by moxyPresenter {
        AuthorizationPresenter(App.instance.router,
            RoomRepoImpl(App.instance.newsDb.accountsDao()),SharedPreferenceAccount())
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
            presenter.openScreenWebView(getString(R.string.teamSite))
        }

        binding.backMainScreen.setOnClickListener {
            presenter.openMain()
        }

        binding.changeRegisterButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.root, changeBounds)
            presenter.changeRegisterAnim()
            if (checkNullRegisterData()) {
                binding.registerButton.alpha = 1F
            }
        }

        binding.changeSignButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.root, changeBounds)
            presenter.changeLoginAnim()
            if (checkNullSignUpData()) {
                binding.signInButton.alpha = 1F
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
                Log.d("RxTextView", it.localizedMessage)
            })

        RxTextView.textChanges(binding.userNameEditText)
            .filter { it.toString().isNotEmpty() }
            .skip(4)
            .subscribe({
                presenter.loginValidation(it)
            }, {
                Log.d("RxTextView", it.localizedMessage)
            })

        RxTextView.textChanges(binding.passwordRegisterEditText)
            .filter { it.toString().isNotEmpty() }
            .skip(6)
            .subscribe({
                presenter.passwordRegisterValidation(it)
            }, {
                Log.d("RxTextView", it.localizedMessage)
            })

        RxTextView.textChanges(binding.userNameRegisterEditText)
            .filter { it.toString().isNotEmpty() }
            .skip(4)
            .subscribe({
                Log.d("PASSWORD", it.toString())
                presenter.loginRegisterValidation(it)
            }, {
                Log.d("RxTextView", it.localizedMessage)
            })


        RxTextView.textChanges(binding.emailRegisterEditText)
            .filter { it.toString().isNotEmpty() }
            .skip(5)
            .subscribe({
                presenter.emailRegisterValidation(it)
            }, {
                Log.d("RxTextView", it.localizedMessage)
            })
    }


    override fun errorSignIn() {
        binding.passwordTextInput.error = "Invalid password"
    }

    override fun errorRegister() {
        Log.d("PASSWORD", "errorRegister() called")
        binding.userNameRegisterTextInput.error = "Check your login"
        binding.emailRegisterTextInput.error = "Check your email"
        binding.root.showSnackBarError("Email and username must be unique", "", {})
    }

    override fun successSignIn(id: Int) {
        binding.root.showSnackBarError("Successful authorization", "", {})
        presenter.openScreenProfile(id)
      //  (requireActivity() as BottomNavigationSetIcon).setDefault()
    }

    override fun successRegister(id: Int) {
        binding.root.showSnackBarError("Account created successfully", "", {})
        presenter.openScreenProfile(id)
       // (requireActivity() as BottomNavigationSetIcon).setDefault()
    }


    override fun sendActivityOpenScreen() {
        (requireActivity() as OpenScreen).openMainScreen()
    }

    override fun emptyAccount() {
        binding.userNameTextInput.error = "This user does not exist"
    }

    override fun setBottomNavigationIcon(checkLogin: String) {
        (requireActivity() as EventLogoutAccountScreen).bottomNavigationSetCurrentAccount(checkLogin)
    }


    override fun loginIsValidate(charSequence: CharSequence?) {
        binding.userNameTextInput.error = null
        userLogin = charSequence.toString()
        if (userPassword != "") {
            binding.signInButton.alpha = 1F
        }
    }

    override fun loginNotValidate() {
        binding.userNameTextInput.error = getString(R.string.errorInputEmail) + "($LOGIN_STR)"
        userLogin = ""
        binding.signInButton.alpha = 0.5F
    }

    override fun passwordIsValidate(it: CharSequence?) {
        binding.passwordTextInput.error = null
        userPassword = it.toString()
        if (userLogin != "") {
            binding.signInButton.alpha = 1F
        }
    }

    override fun passwordNotValidate(it: CharSequence?) {
        userPassword = ""
        binding.passwordTextInput.error =
            getString(R.string.errorInputEmail) + "($PASSWORD_STR)"
        binding.signInButton.alpha = 0.5F
    }

    override fun loginRegisterIsValidate(it: CharSequence?) {
        binding.userNameRegisterTextInput.error = null
        userRegisterLogin = it.toString()
        if (userRegisterPassword != "" && userEmail != "") {
            binding.registerButton.alpha = 1F
        }
    }

    override fun loginRegisterNotValidate() {
        binding.userNameRegisterTextInput.error =
            getString(R.string.errorInputEmail) + "($LOGIN_STR)"
        userRegisterLogin = ""
        binding.registerButton.alpha = 0.5F
    }

    override fun passwordRegisterIsValidate(it: CharSequence?) {
        binding.passwordRegisterTextInput.error = null
        userRegisterPassword = it.toString()
        if (userRegisterLogin != "" && userEmail != "") {
            binding.registerButton.alpha = 1F
        }
    }

    override fun passwordRegisterNotValidate(it: CharSequence?) {
        userRegisterPassword = ""
        binding.passwordRegisterTextInput.error =
            getString(R.string.errorInputEmail) + "($PASSWORD_STR)"
        binding.registerButton.alpha = 0.5F
    }

    override fun emailRegisterIsValidate(it: CharSequence?) {
        binding.emailRegisterTextInput.error = null
        userEmail = it.toString()
        if (userRegisterLogin != "" && userRegisterPassword != "") {
            binding.registerButton.alpha = 1F
        }
    }

    override fun emailRegisterNotValidate() {
        userEmail = ""
        binding.emailRegisterTextInput.error =
            getString(R.string.errorInputEmail) + "($EMAIL_STR)"
        binding.registerButton.alpha = 0.5F
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
        SpannableStringBuilder(binding.privacyPolicy.text).apply {

            setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(),
                R.color.selectedColor)), 41, 55, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(),
                R.color.selectedColor)), 60, 76, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            setSpan(UnderlineSpan(), 41, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(UnderlineSpan(), 60, 76, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            binding.privacyPolicy.text = this
            removeSpan(this)
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
        fun getInstance(): AuthorizationFragment {
            return AuthorizationFragment()
        }
    }
}
