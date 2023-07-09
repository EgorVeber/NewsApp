package ru.gb.veber.newsapi.presentation.favorites.viewpager

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
import ru.gb.veber.newsapi.presentation.favorites.viewpager.FavoritesViewPagerAdapter.Companion.FAVORITES_POSITION
import ru.gb.veber.newsapi.presentation.favorites.viewpager.FavoritesViewPagerAdapter.Companion.HISTORY_POSITION
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import ru.gb.veber.ui_common.BUNDLE_ACCOUNT_ID_KEY
import ru.gb.veber.ui_core.databinding.FavoritesViewPagerFragmentBinding
import javax.inject.Inject

class FavoritesViewPagerFragment : Fragment(),
    BackPressedListener {

    private var _binding: FavoritesViewPagerFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val favoritesViewPagerViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[FavoritesViewPagerViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FavoritesViewPagerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.instance.appComponent.inject(this)
        initialization(arguments?.getInt(BUNDLE_ACCOUNT_ID_KEY) ?: ACCOUNT_ID_DEFAULT)
        initViewModel()
    }

    private fun initialization(accountID: Int) {
        binding.favoritesHistoryViewPager.adapter =
            FavoritesViewPagerAdapter(requireActivity(), accountID)
        TabLayoutMediator(
            binding.favoritesHistoryTabLayout,
            binding.favoritesHistoryViewPager
        ) { tab, position ->
            when (position) {
                FAVORITES_POSITION -> {
                    tab.text = getString(UiCoreStrings.favoritesLike)
                }
                HISTORY_POSITION -> tab.text = getString(UiCoreStrings.favoritesHistory)
            }
        }.attach()
    }

    private fun initViewModel() {
        favoritesViewPagerViewModel.subscribe().observe(viewLifecycleOwner) { state ->
            when (state) {
                FavoritesViewPagerViewModel.FavoritesViewPagerState.InitView -> {
                    favoritesViewPagerViewModel.init()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return favoritesViewPagerViewModel.onBackPressedRouter()
    }

    companion object {
        fun getInstance(accountID: Int): FavoritesViewPagerFragment {
            return FavoritesViewPagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(BUNDLE_ACCOUNT_ID_KEY, accountID)
                }
            }
        }
    }
}
