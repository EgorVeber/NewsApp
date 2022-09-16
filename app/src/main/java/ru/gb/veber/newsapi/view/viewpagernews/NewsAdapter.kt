package ru.gb.veber.newsapi.view.viewpagernews

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.gb.veber.newsapi.view.newsitem.FragmentNews

//business entertainment general health science sports technology
 const val ADAPTER_SIZE = 8
 const val BUSINESS = 1
 const val MAIN = 0
 const val CATEGORY_MAIN = "Main"
 const val CATEGORY_BUSINESS = "Business"
 const val ENTERTAINMENT = 2
 const val CATEGORY_ENTERTAINMENT = "Entertainment"
 const val GENERAL = 3
 const val CATEGORY_GENERAL = "General"
 const val HEALTH = 4
 const val CATEGORY_HEALTH = "Health"
 const val SCIENCE = 5
 const val CATEGORY_SCIENCE = "Science"
 const val SPORTS = 6
 const val CATEGORY_SPORTS = "Sports"
 const val TECHNOLOGY = 7
 const val CATEGORY_TECHNOLOGY = "Technology"


class NewsAdapter(fragmentManager: FragmentActivity) :
    FragmentStateAdapter(fragmentManager) {

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
                else -> ""
            }
        return FragmentNews.getInstance(category)
    }
}