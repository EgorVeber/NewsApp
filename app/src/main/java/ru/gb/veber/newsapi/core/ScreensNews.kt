package ru.gb.veber.newsapi.core

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.gb.veber.newsapi.model.HistorySelect
import ru.gb.veber.newsapi.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerFragment
import ru.gb.veber.newsapi.view.profile.ProfileFragment
import ru.gb.veber.newsapi.view.profile.account.AccountFragment
import ru.gb.veber.newsapi.view.profile.account.settings.EditAccountFragment
import ru.gb.veber.newsapi.view.profile.authorization.AuthorizationFragment
import ru.gb.veber.newsapi.view.search.SearchNewsFragment
import ru.gb.veber.newsapi.view.search.searchnews.AllNewsFragment
import ru.gb.veber.newsapi.view.sources.FragmentSources
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerFragment
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


data class AllNewsScreen(private val accountId: Int, private val historySelect: HistorySelect) :
    FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return AllNewsFragment.getInstance(accountId, historySelect)
    }
}

data class SearchNewsScreen(private val accountId: Int) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return SearchNewsFragment.getInstance(accountId)
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


//data class FragmentNewsScreen(private val category: String) : FragmentScreen {
//    override fun createFragment(factory: FragmentFactory): Fragment {
//        return TopNewsFragment.getInstance(category)
//    }
//}


