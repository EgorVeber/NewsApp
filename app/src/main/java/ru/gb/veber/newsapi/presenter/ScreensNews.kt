package ru.gb.veber.newsapi.presenter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.gb.veber.newsapi.view.FragmentSources

object  FragmentSourcesScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentSources()
    }
}