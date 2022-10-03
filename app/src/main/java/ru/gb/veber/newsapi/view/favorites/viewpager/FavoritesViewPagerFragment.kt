package ru.gb.veber.newsapi.view.favorites.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FavoritesViewPagerFragmentBinding
import ru.gb.veber.newsapi.presenter.FavoritesViewPagerPresenter
import ru.gb.veber.newsapi.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerAdapter.Companion.FAVORITES
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerAdapter.Companion.FAVORITES_POSITION
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerAdapter.Companion.HISTORY
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerAdapter.Companion.HISTORY_POSITION

class FavoritesViewPagerFragment : MvpAppCompatFragment(), FavoritesViewPagerView,
    BackPressedListener {

    private var _binding: FavoritesViewPagerFragmentBinding? = null
    private val binding get() = _binding!!

    private val presenter: FavoritesViewPagerPresenter by moxyPresenter {
        FavoritesViewPagerPresenter(App.instance.router)
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
        initialization(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
    }

    private fun initialization(accountID: Int) {
        binding.favoritesHistoryViewPager.adapter =
            FavoritesViewPagerAdapter(requireActivity(), accountID)
        TabLayoutMediator(binding.favoritesHistoryTabLayout,
            binding.favoritesHistoryViewPager) { tab, position ->
            when (position) {
                FAVORITES_POSITION -> {
                    tab.text = FAVORITES
                }
                HISTORY_POSITION -> tab.text = HISTORY
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
        fun getInstance(accountID: Int): FavoritesViewPagerFragment {
            return FavoritesViewPagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ACCOUNT_ID, accountID)
                }
            }
        }
    }
}