package ru.gb.veber.newsapi.presenter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.gb.veber.newsapi.view.FragmentProfile
import ru.gb.veber.newsapi.view.FragmentSources
import ru.gb.veber.newsapi.view.news.FragmentNews

object  FragmentSourcesScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentSources()
    }
}

object  FragmentNewsScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentNews()
    }
}

object  FragmentProfileScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentProfile()
    }
}