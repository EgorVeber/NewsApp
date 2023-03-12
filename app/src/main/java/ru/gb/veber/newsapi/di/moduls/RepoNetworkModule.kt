package ru.gb.veber.newsapi.di.moduls

import dagger.Module
import dagger.Provides
import ru.gb.veber.newsapi.data.NewsApi
import ru.gb.veber.newsapi.domain.repository.NewsRepo
import ru.gb.veber.newsapi.data.repository.NewsRepoImpl
import javax.inject.Singleton

@Module
object RepoNetworkModule {
    @Provides
    @Singleton
    fun provideRepoNetwork(newsApi: NewsApi): NewsRepo {
        return NewsRepoImpl(newsApi)
    }
}
