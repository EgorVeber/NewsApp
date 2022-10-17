package ru.gb.veber.newsapi.view.favorites.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.gb.veber.newsapi.view.favorites.FavoritesFragment


class FavoritesViewPagerAdapter(fragmentManager: FragmentActivity, private val accountID: Int) :
    FragmentStateAdapter(fragmentManager) {

    companion object {
        const val ADAPTER_SIZE = 2
        const val FAVORITES_POSITION = 0
        const val FAVORITES = "Favorites"
        const val HISTORY_POSITION = 1
        const val HISTORY = "History"
    }

    override fun getItemCount(): Int {
        return ADAPTER_SIZE
    }

    override fun createFragment(position: Int): Fragment {
        var page =
            when (position) {
                FAVORITES_POSITION -> FAVORITES
                HISTORY_POSITION -> HISTORY
                else -> FAVORITES
            }
        return FavoritesFragment.getInstance(page, accountID)
    }
}