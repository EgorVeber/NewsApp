package ru.gb.veber.newsapi.presentation.topnews.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import ru.gb.veber.newsapi.common.UiCoreStrings
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.presentation.activity.callbackhell.BackPressedListener
import ru.gb.veber.newsapi.presentation.topnews.viewpager.TopNewsViewPagerAdapter.Companion.BUSINESS
import ru.gb.veber.newsapi.presentation.topnews.viewpager.TopNewsViewPagerAdapter.Companion.ENTERTAINMENT
import ru.gb.veber.newsapi.presentation.topnews.viewpager.TopNewsViewPagerAdapter.Companion.GENERAL
import ru.gb.veber.newsapi.presentation.topnews.viewpager.TopNewsViewPagerAdapter.Companion.HEALTH
import ru.gb.veber.newsapi.presentation.topnews.viewpager.TopNewsViewPagerAdapter.Companion.SCIENCE
import ru.gb.veber.newsapi.presentation.topnews.viewpager.TopNewsViewPagerAdapter.Companion.SPORTS
import ru.gb.veber.newsapi.presentation.topnews.viewpager.TopNewsViewPagerAdapter.Companion.TECHNOLOGY
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import ru.gb.veber.ui_common.BUNDLE_ACCOUNT_ID_KEY
import ru.gb.veber.ui_core.databinding.TopNewsViewPagerFragmentBinding
import javax.inject.Inject

class TopNewsViewPagerFragment : Fragment(),
    BackPressedListener, EventTopNews {

    private var _binding: TopNewsViewPagerFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val topNewsViewPagerViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[TopNewsViewPagerViewModel::class.java]
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
        App.instance.appComponent.inject(this)
        initialization(arguments?.getInt(BUNDLE_ACCOUNT_ID_KEY) ?: ACCOUNT_ID_DEFAULT)
    }

    private fun initialization(accountID: Int) {
        binding.viewPager.adapter =
            TopNewsViewPagerAdapter(requireActivity(), accountID)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                BUSINESS -> tab.text = getString(UiCoreStrings.categoryBusiness)
                ENTERTAINMENT -> tab.text = getString(UiCoreStrings.categoryEntertainment)
                GENERAL -> tab.text = getString(UiCoreStrings.categoryGeneral)
                HEALTH -> tab.text = getString(UiCoreStrings.categoryHealth)
                SCIENCE -> tab.text = getString(UiCoreStrings.categoryScience)
                SPORTS -> tab.text = getString(UiCoreStrings.categorySports)
                TECHNOLOGY -> tab.text = getString(UiCoreStrings.categoryTechnology)
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return topNewsViewPagerViewModel.onBackPressedRouter()
    }

    companion object {
        fun getInstance(accountID: Int): TopNewsViewPagerFragment {
            return TopNewsViewPagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(BUNDLE_ACCOUNT_ID_KEY, accountID)
                }
            }
        }
    }

    override fun updateViewPager() {
        binding.viewPager.adapter = TopNewsViewPagerAdapter(requireActivity(),
            arguments?.getInt(BUNDLE_ACCOUNT_ID_KEY) ?: ACCOUNT_ID_DEFAULT
        )
    }
}
