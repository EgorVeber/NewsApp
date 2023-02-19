package ru.gb.veber.newsapi.di

import dagger.Component
import ru.gb.veber.newsapi.view.favorites.FavoritesFragment
import ru.gb.veber.newsapi.di.moduls.AppModule
import ru.gb.veber.newsapi.di.moduls.NavigationModule
import ru.gb.veber.newsapi.di.moduls.NetworkModule
import ru.gb.veber.newsapi.di.moduls.SharedPreferenceModule
import ru.gb.veber.newsapi.di.moduls.RepoNetworkModule
import ru.gb.veber.newsapi.di.moduls.DataBaseModule
import ru.gb.veber.newsapi.di.moduls.ViewModelModule
import ru.gb.veber.newsapi.presenter.AuthorizationPresenter
import ru.gb.veber.newsapi.presenter.SearchNewsPresenter
import ru.gb.veber.newsapi.presenter.TopNewsViewPagerPresenter
import ru.gb.veber.newsapi.presenter.WebViewPresenter
import ru.gb.veber.newsapi.presenter.CustomizeCategoryPresenter
import ru.gb.veber.newsapi.view.activity.ActivityMain
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerFragment
import ru.gb.veber.newsapi.view.profile.ProfileFragment
import ru.gb.veber.newsapi.view.profile.account.AccountFragment
import ru.gb.veber.newsapi.view.profile.account.settings.EditAccountFragment
import ru.gb.veber.newsapi.view.topnews.fragment.TopNewsFragment
import ru.gb.veber.newsapi.view.search.SearchFragment
import ru.gb.veber.newsapi.view.sources.FragmentSources
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
    fun inject(authorizationPresenter: AuthorizationPresenter)
    fun inject(searchFragment: SearchFragment)
    fun inject(favoritesFragment: FavoritesFragment)
    fun inject(profileFragment: ProfileFragment)
    fun inject(topNewsFragment: TopNewsFragment)
    fun inject(searchNewsPresenter: SearchNewsPresenter)
    fun inject(fragmentSources: FragmentSources)
    fun inject(topNewsViewPagerPresenter: TopNewsViewPagerPresenter)
    fun inject(webViewPresenter: WebViewPresenter)
    fun inject(customizeCategoryPresenter: CustomizeCategoryPresenter)
    fun inject(editAccountFragment: EditAccountFragment)
    fun inject(favoritesViewPagerFragment: FavoritesViewPagerFragment)
}