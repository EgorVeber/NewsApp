package ru.gb.veber.newsapi.di.moduls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.gb.veber.newsapi.di.ViewModelFactory
import ru.gb.veber.newsapi.di.ViewModelKey
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerViewModel
import ru.gb.veber.newsapi.view.profile.ProfileViewModel
import ru.gb.veber.newsapi.view.profile.account.AccountViewModel
import ru.gb.veber.newsapi.view.profile.account.settings.EditAccountViewModel
import ru.gb.veber.newsapi.view.sources.SourcesViewModel

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
    @ViewModelKey(FavoritesViewPagerViewModel::class)
    internal abstract fun bindFavoritesViewPagerViewModel(viewModel: FavoritesViewPagerViewModel): ViewModel

}

