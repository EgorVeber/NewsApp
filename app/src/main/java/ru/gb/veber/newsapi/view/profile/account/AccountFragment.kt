package ru.gb.veber.newsapi.view.profile.account

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FragmentMprofileBinding
import ru.gb.veber.newsapi.presenter.AccountPresenter
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.profile.ProfileFragment

class AccountFragment : MvpAppCompatFragment(), AccountView, BackPressedListener {

    private var _binding: FragmentMprofileBinding? = null
    private val binding get() = _binding!!

    private val presenter: AccountPresenter by moxyPresenter {
        AccountPresenter(App.instance.router)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMprofileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("arguments", arguments?.getInt(ACCOUNT_ID, 0).toString())
    }

    override fun init() {

        binding.progressBar.max = 100
        binding.progressBar.progress = 10

        binding.textviewsda.setOnClickListener {
            presenter.logout()
        }
    }

    override fun logout() {
        requireActivity().getSharedPreferences(ProfileFragment.FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putInt(
            ProfileFragment.ACCOUNT_ID, 0).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    companion object {
        private const val ACCOUNT_ID = "ACCOUNT_ID"
        fun getInstance(accountID: Int): AccountFragment {
            return AccountFragment().apply {
                arguments = Bundle().apply {
                    putInt(ACCOUNT_ID, accountID)
                }
            }
        }
    }
}