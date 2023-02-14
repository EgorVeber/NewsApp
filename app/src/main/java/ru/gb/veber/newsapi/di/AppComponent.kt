package ru.gb.veber.newsapi.di

import dagger.Component
import ru.gb.veber.newsapi.di.moduls.*
import ru.gb.veber.newsapi.presenter.*
import ru.gb.veber.newsapi.view.activity.ActivityMain
import ru.gb.veber.newsapi.view.profile.account.settings.EditAccountFragment
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
        DataBaseModule::class,
        ViewModelModule::class]
)
interface AppComponent {
    fun inject(mainActivity: ActivityMain)
    fun inject(mainPresenter: ActivityPresenter)
    fun inject(account: AccountPresenter)
    fun inject(authorizationPresenter: AuthorizationPresenter)
    fun inject(topNewsPresenter: TopNewsPresenter)
    fun inject(favoritesPresenter: FavoritesPresenter)
    fun inject(favoritesViewPagerPresenter: FavoritesViewPagerPresenter)
    fun inject(profilePresenter: ProfilePresenter)
    fun inject(searchPresenter: SearchPresenter)
    fun inject(searchNewsPresenter: SearchNewsPresenter)
    fun inject(sourcesPresenter: SourcesPresenter)
    fun inject(topNewsViewPagerPresenter: TopNewsViewPagerPresenter)
    fun inject(webViewPresenter: WebViewPresenter)
    fun inject(customizeCategoryPresenter: CustomizeCategoryPresenter)
    fun inject(editAccountFragment: EditAccountFragment)
}