package ru.gb.veber.newsapi.di

import dagger.Component
import ru.gb.veber.newsapi.di.moduls.AppModule
import ru.gb.veber.newsapi.di.moduls.DataBaseModule
import ru.gb.veber.newsapi.di.moduls.NavigationModule
import ru.gb.veber.newsapi.di.moduls.NetworkModule
import ru.gb.veber.newsapi.di.moduls.RepoNetworkModule
import ru.gb.veber.newsapi.di.moduls.SharedPreferenceModule
import ru.gb.veber.newsapi.di.moduls.ViewModelModule
import ru.gb.veber.newsapi.presentation.account.AccountFragment
import ru.gb.veber.newsapi.presentation.activity.ActivityMain
import ru.gb.veber.newsapi.presentation.favorites.FavoritesFragment
import ru.gb.veber.newsapi.presentation.favorites.viewpager.FavoritesViewPagerFragment
import ru.gb.veber.newsapi.presentation.keymanagement.KeysManagementFragment
import ru.gb.veber.newsapi.presentation.profile.ProfileFragment
import ru.gb.veber.newsapi.presentation.profile.account.settings.EditAccountFragment
import ru.gb.veber.newsapi.presentation.profile.account.settings.customize.CustomizeCategoryFragment
import ru.gb.veber.newsapi.presentation.profile.authorization.AuthorizationFragment
import ru.gb.veber.newsapi.presentation.search.SearchFragment
import ru.gb.veber.newsapi.presentation.searchnews.SearchNewsFragment
import ru.gb.veber.newsapi.presentation.sources.SourcesFragment
import ru.gb.veber.newsapi.presentation.topnews.fragment.TopNewsFragment
import ru.gb.veber.newsapi.presentation.topnews.viewpager.TopNewsViewPagerFragment
import ru.gb.veber.newsapi.presentation.webview.WebViewFragment
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NavigationModule::class,
        AppModule::class,
        SharedPreferenceModule::class,
        NetworkModule::class,
        RepoNetworkModule::class,
        DataBaseModule::class,
        ViewModelModule::class]
)
interface AppComponent {
    fun inject(mainActivity: ActivityMain)
    fun inject(accountFragment: AccountFragment)
    fun inject(searchFragment: SearchFragment)
    fun inject(favoritesFragment: FavoritesFragment)
    fun inject(profileFragment: ProfileFragment)
    fun inject(topNewsFragment: TopNewsFragment)
    fun inject(searchNewsFragment: SearchNewsFragment)
    fun inject(sourcesFragment: SourcesFragment)
    fun inject(webViewFragment: WebViewFragment)
    fun inject(editAccountFragment: EditAccountFragment)
    fun inject(favoritesViewPagerFragment: FavoritesViewPagerFragment)
    fun inject(authorizationFragment: AuthorizationFragment)
    fun inject(topNewsViewPagerFragment: TopNewsViewPagerFragment)
    fun inject(customizeCategoryFragment: CustomizeCategoryFragment)
    fun inject(keysManagementFragment: KeysManagementFragment)
}
