package ru.gb.veber.newsapi.di.moduls

import dagger.Module
import dagger.Provides
import ru.gb.veber.newsapi.model.network.NewsApi
import ru.gb.veber.newsapi.model.repository.network.NewsRepo
import ru.gb.veber.newsapi.model.repository.network.NewsRepoImpl
import javax.inject.Singleton

@Module
object RepoNetworkModule {
    @Provides
    @Singleton
    fun provideRepoNetwork(newsApi: NewsApi): NewsRepo {
        return NewsRepoImpl(newsApi)
    }
}
