package ru.gb.veber.newsapi.common.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.domain.models.HistorySelect
import ru.gb.veber.newsapi.presentation.topnews.viewpager.TopNewsViewPagerFragment
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerFragment
import ru.gb.veber.newsapi.view.profile.ProfileFragment
import ru.gb.veber.newsapi.view.profile.account.AccountFragment
import ru.gb.veber.newsapi.view.profile.account.settings.CustomizeCategoryFragment
import ru.gb.veber.newsapi.view.profile.account.settings.EditAccountFragment
import ru.gb.veber.newsapi.view.profile.authorization.AuthorizationFragment
import ru.gb.veber.newsapi.presentation.search.SearchFragment
import ru.gb.veber.newsapi.presentation.searchnews.SearchNewsFragment
import ru.gb.veber.newsapi.presentation.sources.FragmentSources
import ru.gb.veber.newsapi.view.webview.WebViewFragment

data class ProfileScreen(private val accountId: Int) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return ProfileFragment.getInstance(accountId)
    }
}

data class AccountScreen(private val accountId: Int) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return AccountFragment.getInstance(accountId)
    }
}

object AuthorizationScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return AuthorizationFragment.getInstance()
    }
}


data class SourcesScreen(private val accountId: Int) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentSources.getInstance(accountId)
    }
}

data class TopNewsViewPagerScreen(private val accountId: Int) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return TopNewsViewPagerFragment.getInstance(accountId)
    }
}

data class FavoritesViewPagerScreen(private val accountId: Int) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FavoritesViewPagerFragment.getInstance(accountId)
    }
}


data class SearchNewsScreen(private val accountId: Int, private val historySelect: HistorySelect) :
    FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return SearchNewsFragment.getInstance(accountId, historySelect)
    }
}

data class SearchScreen(private val accountId: Int) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return SearchFragment.getInstance(accountId)
    }
}

data class WebViewScreen(private val url: String) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return WebViewFragment.getInstance(url)
    }
}

data class EditAccountScreen(private val accountId: Int) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return EditAccountFragment.getInstance(Bundle().apply {
            putInt(ACCOUNT_ID, accountId)
        })
    }
}

data class CustomizeCategoryScreen(private val accountId: Int) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return CustomizeCategoryFragment.getInstance()
    }
}

