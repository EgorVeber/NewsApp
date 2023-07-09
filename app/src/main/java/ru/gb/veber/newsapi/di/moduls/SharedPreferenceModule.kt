package ru.gb.veber.newsapi.di.moduls


import dagger.Module
import dagger.Provides
import ru.gb.veber.newsapi.common.PrefsAccountHelper
import javax.inject.Singleton

@Module
object SharedPreferenceModule {

    @Singleton
    @Provides
    fun provideSharedPreferenceAccount(): PrefsAccountHelper {
        return PrefsAccountHelper
    }
}
