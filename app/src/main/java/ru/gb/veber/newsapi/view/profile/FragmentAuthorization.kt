package ru.gb.veber.newsapi.view.profile

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.UnderlineSpan
import android.transition.Transition
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.transition.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FragmentSignInBinding
import ru.gb.veber.newsapi.model.repository.RoomRepoImpl
import ru.gb.veber.newsapi.presenter.FragmentAuthorizationPresenter
import ru.gb.veber.newsapi.utils.hide
import ru.gb.veber.newsapi.utils.show
import ru.gb.veber.newsapi.utils.showSnackBarError
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.activity.OpenScreen

class FragmentAuthorization : MvpAppCompatFragment(), FragmentAuthorizationView,
    BackPressedListener {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

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

        var spanableStringBuilder =
            SpannableStringBuilder(getString(R.string.TermsOfUserRegister))
        spanableStringBuilder.setSpan(ForegroundColorSpan(resources.getColor(R.color.selectedColor)),
            41, 55, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanableStringBuilder.setSpan(ForegroundColorSpan(resources.getColor(R.color.selectedColor)),
            60, 76, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanableStringBuilder.setSpan(UnderlineSpan(),41,55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanableStringBuilder.setSpan(UnderlineSpan(),60,76, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.teamSign.text = spanableStringBuilder
        spanableStringBuilder.removeSpan(spanableStringBuilder)


        binding.teamSign.setOnClickListener {
            presenter.openScreenWebView(getString(R.string.teamSite))
        }

        binding.homePageBack.setOnClickListener {
            presenter.openMain()
        }


        var constraintSetLogin = ConstraintSet()
        constraintSetLogin.clone(binding.root)

        binding.changeRegisterButton.setOnClickListener {
            var changeBounds = ChangeBounds()
            changeBounds.duration = 650L
            changeBounds.interpolator = AnticipateOvershootInterpolator(1F)
            TransitionManager.beginDelayedTransition(binding.root, changeBounds)

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






        binding.changeSignButton.setOnClickListener {
            var changeBounds = ChangeBounds()
            changeBounds.duration = 650L
            changeBounds.interpolator = AnticipateOvershootInterpolator(1F)
            TransitionManager.beginDelayedTransition(binding.root, changeBounds)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    companion object {

        fun getInstance(): FragmentAuthorization {
            return FragmentAuthorization()
        }
    }
}