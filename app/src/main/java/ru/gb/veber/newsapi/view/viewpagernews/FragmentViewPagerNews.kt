package ru.gb.veber.newsapi.view.viewpagernews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FragmentNewsBinding
import ru.gb.veber.newsapi.presenter.FragmentViewPagerNewsPresenter
import ru.gb.veber.newsapi.view.activity.BackPressedListener

class FragmentViewPagerNews : MvpAppCompatFragment(), FragmentViewPagerNewsView,
    BackPressedListener {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private val presenter: FragmentViewPagerNewsPresenter by moxyPresenter {
        FragmentViewPagerNewsPresenter(App.instance.router)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = NewsAdapter(requireActivity())
         TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                BUSINESS -> tab.text = CATEGORY_BUSINESS
                ENTERTAINMENT -> tab.text = CATEGORY_ENTERTAINMENT
                GENERAL -> tab.text = CATEGORY_GENERAL
                HEALTH -> tab.text = CATEGORY_HEALTH
                SCIENCE -> tab.text = CATEGORY_SCIENCE
                SPORTS -> tab.text = CATEGORY_SPORTS
                TECHNOLOGY -> tab.text = CATEGORY_TECHNOLOGY
            }
        }.attach()
    }

    override fun init() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    companion object {
        fun getInstance(): FragmentViewPagerNews {
            return FragmentViewPagerNews()
        }
    }
}