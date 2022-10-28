package ru.gb.veber.newsapi.di


import dagger.Module
import dagger.Provides
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.network.ChangeRequestHelper
import javax.inject.Singleton

@Module
object ChangeRequestModule {

    @Singleton
    @Provides
    fun provideChangeRequestHelper(): ChangeRequestHelper {
        return ChangeRequestHelper
    }
}
