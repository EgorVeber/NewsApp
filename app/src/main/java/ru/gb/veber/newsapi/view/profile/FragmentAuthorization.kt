package ru.gb.veber.newsapi.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FragmentSignInBinding
import ru.gb.veber.newsapi.model.repository.RoomRepoImpl
import ru.gb.veber.newsapi.presenter.FragmentAuthorizationPresenter
import ru.gb.veber.newsapi.utils.showSnackBarError
import ru.gb.veber.newsapi.view.activity.BackPressedListener

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

        binding.signInButton.setOnClickListener {
            presenter.openScreenProfile()
        }

        binding.signUpButton.setOnClickListener {
            var username = binding.userNameEditText.text.toString()
            var email = binding.emailEditText.text.toString()
            var password = binding.passwordEditText.text.toString()
            presenter.createAccount(username,email,password)
        }
    }

    override fun success() {
        binding.root.showSnackBarError("Success create account","",{})
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