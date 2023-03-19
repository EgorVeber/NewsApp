package ru.gb.veber.newsapi.di.moduls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.gb.veber.newsapi.di.ViewModelFactory
import ru.gb.veber.newsapi.di.ViewModelKey
import ru.gb.veber.newsapi.presentation.account.AccountViewModel
import ru.gb.veber.newsapi.presentation.activity.ActivityMainViewModel
import ru.gb.veber.newsapi.presentation.search.SearchViewModel
import ru.gb.veber.newsapi.presentation.searchnews.SearchNewsViewModel
import ru.gb.veber.newsapi.presentation.sources.SourcesViewModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.TopNewsViewModel
import ru.gb.veber.newsapi.presentation.topnews.viewpager.TopNewsViewPagerViewModel
import ru.gb.veber.newsapi.presentation.webview.WebViewViewModel
import ru.gb.veber.newsapi.view.favorites.FavoritesViewModel
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerViewModel
import ru.gb.veber.newsapi.view.profile.ProfileViewModel
import ru.gb.veber.newsapi.view.profile.account.settings.EditAccountViewModel
import ru.gb.veber.newsapi.view.profile.account.settings.customize.CustomizeCategoryViewModel
import ru.gb.veber.newsapi.view.profile.authorization.AuthorizationViewModel


@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(EditAccountViewModel::class)
    internal abstract fun bindEditAccountViewModel(viewModel: EditAccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ActivityMainViewModel::class)
    internal abstract fun bindActivityMainViewModel(viewModel: ActivityMainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoritesViewModel::class)
    internal abstract fun bindFavoritesViewModel(viewModel: FavoritesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SourcesViewModel::class)
    internal abstract fun bindSourcesViewModel(viewModel: SourcesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    internal abstract fun bindProfileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    internal abstract fun bindAccountViewModel(viewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    internal abstract fun bindSearchViewModel(viewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchNewsViewModel::class)
    internal abstract fun bindSearchNewsViewModel(viewModel: SearchNewsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoritesViewPagerViewModel::class)
    internal abstract fun bindFavoritesViewPagerViewModel(viewModel: FavoritesViewPagerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopNewsViewModel::class)
    internal abstract fun bindTopNewsViewModel(viewModel: TopNewsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WebViewViewModel::class)
    internal abstract fun bindWebViewViewModel(viewModel: WebViewViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthorizationViewModel::class)
    internal abstract fun bindAuthorizationViewModel(viewModel: AuthorizationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopNewsViewPagerViewModel::class)
    internal abstract fun bindTopNewsViewPagerViewModel(viewModel: TopNewsViewPagerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CustomizeCategoryViewModel::class)
    internal abstract fun bindCustomizeCategoryViewModel(viewModel: CustomizeCategoryViewModel): ViewModel
}

