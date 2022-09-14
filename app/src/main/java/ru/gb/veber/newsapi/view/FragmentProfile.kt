package ru.gb.veber.newsapi.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FragmentMprofileBinding
import ru.gb.veber.newsapi.presenter.FragmentProfilePresenter
import ru.gb.veber.newsapi.presenter.FragmentProfileView

class FragmentProfile : MvpAppCompatFragment(), FragmentProfileView,BackPressedListener {

    private var _binding: FragmentMprofileBinding? = null
    private val binding get() = _binding!!

    private val presenter: FragmentProfilePresenter by moxyPresenter {
        FragmentProfilePresenter(App.instance.router)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMprofileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun init() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
      return  presenter.onBackPressedRouter()
    }
}