package ru.gb.veber.newsapi.di

import dagger.Component
import ru.gb.veber.newsapi.di.moduls.AppModule
import ru.gb.veber.newsapi.di.moduls.ChangeRequestModule
import ru.gb.veber.newsapi.di.moduls.DataBaseModule
import ru.gb.veber.newsapi.di.moduls.NavigationModule
import ru.gb.veber.newsapi.di.moduls.NetworkModule
import ru.gb.veber.newsapi.di.moduls.RepoNetworkModule
import ru.gb.veber.newsapi.di.moduls.SharedPreferenceModule
import ru.gb.veber.newsapi.di.moduls.ViewModelModule
import ru.gb.veber.newsapi.presenter.AccountPresenter
import ru.gb.veber.newsapi.presenter.ActivityPresenter
import ru.gb.veber.newsapi.presenter.AuthorizationPresenter
import ru.gb.veber.newsapi.presenter.FavoritesPresenter
import ru.gb.veber.newsapi.presenter.FavoritesViewPagerPresenter
import ru.gb.veber.newsapi.presenter.ProfilePresenter
import ru.gb.veber.newsapi.presenter.SearchNewsPresenter
import ru.gb.veber.newsapi.presenter.SearchPresenter
import ru.gb.veber.newsapi.presenter.SourcesPresenter
import ru.gb.veber.newsapi.presenter.TopNewsPresenter
import ru.gb.veber.newsapi.presenter.TopNewsViewPagerPresenter
import ru.gb.veber.newsapi.presenter.WebViewPresenter
import ru.gb.veber.newsapi.view.activity.ActivityMain
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NavigationModule::class,
        AppModule::class,
        SharedPreferenceModule::class,
        ChangeRequestModule::class,
        NetworkModule::class,
        RepoNetworkModule::class,
        DataBaseModule::class]
)
interface AppComponent {
    fun inject(mainActivity: ActivityMain)
    fun inject(mainPresenter: ActivityPresenter)
    fun inject(account: AccountPresenter)
    fun inject(authorizationPresenter: AuthorizationPresenter)
    fun inject(editAccountPresenter: EditAccountPresenter)
    fun inject(topNewsPresenter: TopNewsPresenter)
    fun inject(favoritesPresenter: FavoritesPresenter)
    fun inject(favoritesViewPagerPresenter: FavoritesViewPagerPresenter)
    fun inject(profilePresenter: ProfilePresenter)
    fun inject(searchPresenter: SearchPresenter)
    fun inject(searchNewsPresenter: SearchNewsPresenter)
    fun inject(sourcesPresenter: SourcesPresenter)
    fun inject(topNewsViewPagerPresenter: TopNewsViewPagerPresenter)
    fun inject(webViewPresenter: WebViewPresenter)
}