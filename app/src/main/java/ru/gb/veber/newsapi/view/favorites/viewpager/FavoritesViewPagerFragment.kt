package ru.gb.veber.newsapi.view.favorites.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import moxy.MvpAppCompatFragment
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FavoritesViewPagerFragmentBinding
import ru.gb.veber.newsapi.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerAdapter.Companion.FAVORITES_POSITION
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerAdapter.Companion.HISTORY_POSITION
import javax.inject.Inject

class FavoritesViewPagerFragment : MvpAppCompatFragment(),
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
        initialization(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
        initViewModel()
    }

    private fun initialization(accountID: Int) {
        binding.favoritesHistoryViewPager.adapter =
            FavoritesViewPagerAdapter(requireActivity(), accountID)
        TabLayoutMediator(binding.favoritesHistoryTabLayout,
            binding.favoritesHistoryViewPager) { tab, position ->
            when (position) {
                FAVORITES_POSITION -> {
                    tab.text = getString(R.string.favoritesLike)
                }
                HISTORY_POSITION -> tab.text = getString(R.string.favoritesHistory)
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
                    putInt(ACCOUNT_ID, accountID)
                }
            }
        }
    }
}