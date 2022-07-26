package ru.gb.veber.newsapi.view.topnews.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.TopNewsViewPagerFragmentBinding
import ru.gb.veber.newsapi.presenter.TopNewsViewPagerPresenter
import ru.gb.veber.newsapi.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.BUSINESS
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.ENTERTAINMENT
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.GENERAL
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.HEALTH
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.SCIENCE
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.SPORTS
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.TECHNOLOGY


interface EventTopNews {
    fun updateViewPager()
}

class TopNewsViewPagerFragment : MvpAppCompatFragment(), TopNewsViewPagerView,
    BackPressedListener, EventTopNews {

    private var _binding: TopNewsViewPagerFragmentBinding? = null
    private val binding get() = _binding!!

    private val presenter: TopNewsViewPagerPresenter by moxyPresenter {
        TopNewsViewPagerPresenter().apply {
            App.instance.appComponent.inject(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = TopNewsViewPagerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
    }

    private fun initialization(accountID: Int) {
        binding.viewPager.adapter =
            TopNewsViewPagerAdapter(requireActivity(), accountID)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                BUSINESS -> tab.text = getString(R.string.categoryBusiness)
                ENTERTAINMENT -> tab.text = getString(R.string.categoryEntertainment)
                GENERAL -> tab.text = getString(R.string.categoryGeneral)
                HEALTH -> tab.text = getString(R.string.categoryHealth)
                SCIENCE -> tab.text = getString(R.string.categoryScience)
                SPORTS -> tab.text = getString(R.string.categorySports)
                TECHNOLOGY -> tab.text = getString(R.string.categoryTechnology)
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
        fun getInstance(accountID: Int): TopNewsViewPagerFragment {
            return TopNewsViewPagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ACCOUNT_ID, accountID)
                }
            }
        }
    }

    override fun updateViewPager() {
        binding.viewPager.adapter = TopNewsViewPagerAdapter(requireActivity(),
            arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
    }
}