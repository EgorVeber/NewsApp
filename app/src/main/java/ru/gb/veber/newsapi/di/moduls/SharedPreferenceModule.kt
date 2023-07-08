package ru.gb.veber.newsapi.di.moduls


import dagger.Module
import dagger.Provides
import ru.gb.veber.newsapi.data.AccountDataSource
import javax.inject.Singleton

@Module
object SharedPreferenceModule {

    @Singleton
    @Provides
    fun provideSharedPreferenceAccount(): AccountDataSource {
        return AccountDataSource
    }
}
