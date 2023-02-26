package ru.gb.veber.newsapi.view.topnews.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.gb.veber.newsapi.view.topnews.fragment.TopNewsFragment


class TopNewsViewPagerAdapter(
    fragmentManager: FragmentActivity,
    private val accountID: Int,
) :
    FragmentStateAdapter(fragmentManager) {
    companion object {
        const val ADAPTER_SIZE = 7
        const val BUSINESS = 1
        const val CATEGORY_BUSINESS = "Business"
        const val ENTERTAINMENT = 2
        const val CATEGORY_ENTERTAINMENT = "Entertainment"
        const val GENERAL = 0
        const val CATEGORY_GENERAL = "General"
        const val HEALTH = 3
        const val CATEGORY_HEALTH = "Health"
        const val SCIENCE = 4
        const val CATEGORY_SCIENCE = "Science"
        const val SPORTS = 5
        const val CATEGORY_SPORTS = "Sports"
        const val TECHNOLOGY = 6
        const val CATEGORY_TECHNOLOGY = "Technology"
    }

    override fun getItemCount(): Int {
        return ADAPTER_SIZE
    }

    override fun createFragment(position: Int): Fragment {
        var category =
            when (position) {
                BUSINESS -> CATEGORY_BUSINESS
                ENTERTAINMENT -> CATEGORY_ENTERTAINMENT
                GENERAL -> CATEGORY_GENERAL
                HEALTH -> CATEGORY_HEALTH
                SCIENCE -> CATEGORY_SCIENCE
                SPORTS -> CATEGORY_SPORTS
                TECHNOLOGY -> CATEGORY_TECHNOLOGY
                else -> CATEGORY_GENERAL
            }
        return TopNewsFragment.getInstance(category, accountID)
    }
}