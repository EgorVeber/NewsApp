package ru.gb.veber.newsapi.di.moduls


import dagger.Module
import dagger.Provides
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
