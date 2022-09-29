package ru.gb.veber.newsapi.presenter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.gb.veber.newsapi.view.newsitem.FragmentNews
import ru.gb.veber.newsapi.view.newswebview.FragmentNewsWebView
import ru.gb.veber.newsapi.view.profile.FragmentAuthorization
import ru.gb.veber.newsapi.view.profile.FragmentProfile
import ru.gb.veber.newsapi.view.profile.FragmentProfileMain
import ru.gb.veber.newsapi.view.sources.FragmentSources
import ru.gb.veber.newsapi.view.viewpagernews.FragmentViewPagerNews

object FragmentSourcesScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentSources.getInstance()
    }
}

object FragmentAuthorizationScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentAuthorization.getInstance()
    }
}


object FragmentProfileMainScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentProfileMain.getInstance()
    }
}

data class FragmentNewsScreen(private val category: String) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentNews.getInstance(category)
    }
}

data class FragmentProfileScreen(private val accountId: Int) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentProfile.getInstance(accountId)
    }
}

object FragmentViewPagerNewsScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentViewPagerNews.getInstance()
    }
}

data class FragmentNewsWebViewScreen(private val url: String) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return FragmentNewsWebView.getInstance(url)
    }
}

