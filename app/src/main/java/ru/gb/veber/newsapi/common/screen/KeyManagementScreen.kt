package ru.gb.veber.newsapi.common.screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.gb.veber.newsapi.presentation.keymanagement.KeysManagementFragment

data class KeyManagementScreen(private val accountId: Int) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return KeysManagementFragment.getInstance(accountId)
    }
}
