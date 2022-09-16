package ru.gb.veber.newsapi.presenter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.gb.veber.newsapi.view.profile.FragmentProfile
import ru.gb.veber.newsapi.view.sources.FragmentSources
import ru.gb.veber.newsapi.view.newsitem.FragmentNews
import ru.gb.veber.newsapi.view.viewpagernews.FragmentViewPagerNews

object  FragmentSourcesScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentSources.getInstance()
    }
}

data class  FragmentNewsScreen(private val category:String) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentNews.getInstance(category)
    }
}

object  FragmentProfileScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentProfile.getInstance()
    }
}

object  FragmentViewPagerNewsScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentViewPagerNews.getInstance()
    }
}
