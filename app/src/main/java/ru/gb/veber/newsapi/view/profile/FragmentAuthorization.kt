package ru.gb.veber.newsapi.view.profile

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
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


        binding.homePageBack.setOnClickListener {
            presenter.openMain()
        }

//        binding.signInButton.setOnClickListener {
//            presenter.openScreenProfile()
//            // (requireActivity() as TestDate).getIdFragment(5)
//        }
//
//        binding.changeRegisterButton.setOnClickListener {
//            TransitionSet().also { transition ->
//                transition.duration = 500L
//                transition.addTransition(Fade())
//                transition.addTransition(Slide(Gravity.END))
//                TransitionManager.beginDelayedTransition(binding.root, transition)
//            }
//            binding.group1.hide()
//            binding.group2.show()
//        }
//
//        binding.changeSignButton.setOnClickListener {
//            TransitionSet().also { transition ->
//                transition.duration = 500L
//                transition.addTransition(Fade())
//                transition.addTransition(Slide(Gravity.START))
//                TransitionManager.beginDelayedTransition(binding.root, transition)
//            }
//            binding.group1.show()
//            binding.group2.hide()
//        }


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