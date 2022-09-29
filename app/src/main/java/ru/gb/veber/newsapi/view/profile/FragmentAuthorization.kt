package ru.gb.veber.newsapi.view.profile

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
import ru.gb.veber.newsapi.databinding.FragmentSignInBinding
import ru.gb.veber.newsapi.model.repository.RoomRepoImpl
import ru.gb.veber.newsapi.presenter.FragmentAuthorizationPresenter
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.activity.OpenScreen


class FragmentAuthorization : MvpAppCompatFragment(), FragmentAuthorizationView,
    BackPressedListener {

    private var _binding: FragmentSignInBinding? = null
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

    private val presenter: FragmentAuthorizationPresenter by moxyPresenter {
        FragmentAuthorizationPresenter(App.instance.router,
            RoomRepoImpl(App.instance.newsDb.accountsDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun init() {

        setSpanRegulationsTv()
        constraintSetLogin.clone(binding.root)
        rxTextChangerValidation()


        binding.teamSign.setOnClickListener {
            presenter.openScreenWebView(getString(R.string.teamSite))
        }

        binding.homePageBack.setOnClickListener {
            presenter.openMain()
        }

        binding.changeRegisterButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.root, changeBounds)
            presenter.changeRegisterAnim()
        }

        binding.changeSignButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.root, changeBounds)
            presenter.changeLoginAnim()
            if (userLogin != "" && userPassword != "") {
                binding.signInButton.alpha = 1F
            }
        }

        binding.signInButton.setOnClickListener {
            if (userLogin != "" && userPassword != "") {
                presenter.getPasswordToLogin(userLogin, userPassword)
            }
        }
    }

    private fun rxTextChangerValidation() {

        RxTextView.textChanges(binding.passwordEditText)
            .skip(6)
            .filter { it.toString().isNotEmpty() }
            .subscribe({
                presenter.passwordValidation(it)
            }, {
                Log.d("RxTextView", it.localizedMessage)
            })

        RxTextView.textChanges(binding.userNameEditText)
            .skip(5)
            .filter { it.toString().isNotEmpty() }
            .subscribe({
                presenter.loginValidation(it)
            }, {
                Log.d("RxTextView", it.localizedMessage)
            })
    }

    override fun success() {
        binding.root.showSnackBarError("Success create account", "", {})
    }

    override fun error() {
        binding.root.showSnackBarError("Such user already exists", "", {})
    }

    override fun sendActivityOpenScreen() {
        (requireActivity() as OpenScreen).openMainScreen()
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

        constraintSetLogin.clear(R.id.teamSign, ConstraintSet.TOP)
        constraintSetLogin.connect(R.id.teamSign,
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
        constraintSetLogin.clear(R.id.teamSign, ConstraintSet.TOP)
        constraintSetLogin.connect(R.id.teamSign, ConstraintSet.TOP,
            R.id.changeRegisterButton,
            ConstraintSet.BOTTOM)
        constraintSetLogin.applyTo(binding.root)
    }

    private fun setSpanRegulationsTv() {
        SpannableStringBuilder(binding.teamSign.text).apply {

            setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(),
                R.color.selectedColor)), 41, 55, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(),
                R.color.selectedColor)), 60, 76, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            setSpan(UnderlineSpan(), 41, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(UnderlineSpan(), 60, 76, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            binding.teamSign.text = this
            removeSpan(this)
        }
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
        fun getInstance(): FragmentAuthorization {
            return FragmentAuthorization()
        }
    }
}
