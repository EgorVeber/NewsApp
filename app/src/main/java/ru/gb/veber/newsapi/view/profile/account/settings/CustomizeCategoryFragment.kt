package ru.gb.veber.newsapi.view.profile.account.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moxy.MvpAppCompatFragment
import moxy.MvpView
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.CustomizeCategoryFragmentBinding
import ru.gb.veber.newsapi.presenter.CustomizeCategoryPresenter
import ru.gb.veber.newsapi.view.activity.BackPressedListener

class CustomizeCategoryFragment : MvpAppCompatFragment(), MvpView, BackPressedListener {

    private var _binding: CustomizeCategoryFragmentBinding? = null
    private val binding get() = _binding!!

    private val presenter: CustomizeCategoryPresenter by moxyPresenter {
        CustomizeCategoryPresenter().apply {
            App.instance.appComponent.inject(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CustomizeCategoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialize()
    }

    private fun initialize() {
        binding.backAccountScreen.setOnClickListener {
            presenter.backAccountScreen()
        }
    }

    companion object {
        fun getInstance() = CustomizeCategoryFragment()
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}