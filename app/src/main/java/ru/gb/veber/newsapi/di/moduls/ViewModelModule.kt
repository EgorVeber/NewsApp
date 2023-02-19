package ru.gb.veber.newsapi.di.moduls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.gb.veber.newsapi.di.ViewModelFactory
import ru.gb.veber.newsapi.di.ViewModelKey
import ru.gb.veber.newsapi.view.favorites.FavoritesViewModel
import ru.gb.veber.newsapi.view.profile.account.AccountViewModel
import ru.gb.veber.newsapi.view.profile.account.settings.EditAccountViewModel


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
    @ViewModelKey(FavoritesViewModel::class)
    internal abstract fun bindFavoritesViewModel(viewModel: FavoritesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    internal abstract fun bindAccountViewModel(viewModel: AccountViewModel): ViewModel

}