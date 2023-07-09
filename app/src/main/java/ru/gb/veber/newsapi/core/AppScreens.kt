package ru.gb.veber.newsapi.core

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.gb.veber.newsapi.domain.models.HistorySelectModel
import ru.gb.veber.newsapi.presentation.account.AccountFragment
import ru.gb.veber.newsapi.presentation.favorites.viewpager.FavoritesViewPagerFragment
import ru.gb.veber.newsapi.presentation.keymanagement.KeysManagementFragment
import ru.gb.veber.newsapi.presentation.profile.ProfileFragment
import ru.gb.veber.newsapi.presentation.profile.account.settings.EditAccountFragment
import ru.gb.veber.newsapi.presentation.profile.account.settings.customize.CustomizeCategoryFragment
import ru.gb.veber.newsapi.presentation.profile.authorization.AuthorizationFragment
import ru.gb.veber.newsapi.presentation.profile.file.FileFragment
import ru.gb.veber.newsapi.presentation.search.SearchFragment
import ru.gb.veber.newsapi.presentation.searchnews.SearchNewsFragment
import ru.gb.veber.newsapi.presentation.sources.SourcesFragment
import ru.gb.veber.newsapi.presentation.topnews.viewpager.TopNewsViewPagerFragment
import ru.gb.veber.newsapi.presentation.webview.WebViewFragment
data class KeyManagementScreen(private val accountId: Int) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return KeysManagementFragment.getInstance(accountId)
    }
}

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
        return SourcesFragment.getInstance(accountId)
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


data class SearchNewsScreen(
    private val accountId: Int,
    private val historySelectModel: HistorySelectModel,
) :
    FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return SearchNewsFragment.getInstance(accountId, historySelectModel)
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
        return EditAccountFragment.getInstance(accountId)
    }
}

data class CustomizeCategoryScreen(private val accountId: Int) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return CustomizeCategoryFragment.getInstance(accountId)
    }
}

data class FileScreen(private val accountId: Int) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FileFragment.getInstance(accountId)
    }
}
