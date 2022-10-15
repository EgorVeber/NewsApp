package ru.gb.veber.newsapi.view.topnews.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.TopNewsViewPagerFragmentBinding
import ru.gb.veber.newsapi.presenter.TopNewsViewPagerPresenter
import ru.gb.veber.newsapi.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.BUSINESS
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.CATEGORY_BUSINESS
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.CATEGORY_ENTERTAINMENT
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.CATEGORY_GENERAL
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.CATEGORY_HEALTH
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.CATEGORY_SCIENCE
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.CATEGORY_SPORTS
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.CATEGORY_TECHNOLOGY
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.ENTERTAINMENT
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.GENERAL
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.HEALTH
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.SCIENCE
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.SPORTS
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.TECHNOLOGY

class TopNewsViewPagerFragment : MvpAppCompatFragment(), TopNewsViewPagerView,
    BackPressedListener {

    private var _binding: TopNewsViewPagerFragmentBinding? = null
    private val binding get() = _binding!!

    private val presenter: TopNewsViewPagerPresenter by moxyPresenter {
        TopNewsViewPagerPresenter(App.instance.router)
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
        binding.viewPager.adapter = TopNewsViewPagerAdapter(requireActivity(), accountID)
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
        //binding.viewPager.isUserInputEnabled = false
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
}