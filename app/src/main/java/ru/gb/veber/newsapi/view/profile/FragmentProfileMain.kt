package ru.gb.veber.newsapi.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FragmentProfileMainBinding
import ru.gb.veber.newsapi.presenter.FragmentProfileMainPresenter
import ru.gb.veber.newsapi.view.activity.BackPressedListener

class FragmentProfileMain : MvpAppCompatFragment(), FragmentProfileMainView, BackPressedListener {

    private var _binding: FragmentProfileMainBinding? = null
    private val binding get() = _binding!!

    private val presenter: FragmentProfileMainPresenter by moxyPresenter {
        FragmentProfileMainPresenter(App.instance.router)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun init() {
//    requireActivity().getSharedPreferences(FILE_SETTINGS, Context.MODE_PRIVATE).edit().putInt(
//        ACCOUNT_ID, 0).apply()
        val accountId =
            requireActivity().getSharedPreferences(FILE_SETTINGS, AppCompatActivity.MODE_PRIVATE)
                .getInt(ACCOUNT_ID, 0)
        if (accountId != 0) {
            presenter.openScreenProfile(accountId)
        } else {
            presenter.openScreenAuthorization()
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

        const val FILE_SETTINGS = "FILE_SETTINGS"
        const val ACCOUNT_ID = "ACCOUNT_ID"

        fun getInstance(): FragmentProfileMain {
            return FragmentProfileMain()
        }
    }
}