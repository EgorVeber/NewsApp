package ru.gb.veber.newsapi.di


import dagger.Module
import dagger.Provides
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import javax.inject.Singleton

@Module
object SharedPreferenceModule {

    @Singleton
    @Provides
    fun provideSharedPreferenceAccount(): SharedPreferenceAccount {
        return SharedPreferenceAccount
    }
}
