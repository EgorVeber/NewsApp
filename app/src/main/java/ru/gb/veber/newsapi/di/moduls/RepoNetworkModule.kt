package ru.gb.veber.newsapi.di.moduls

import dagger.Module
import dagger.Provides
import ru.gb.veber.newsapi.data.NewsService
import ru.gb.veber.newsapi.data.repository.NewsRepoImpl
import ru.gb.veber.newsapi.domain.repository.NewsRepo
import javax.inject.Singleton

@Module
object RepoNetworkModule {
    @Provides
    @Singleton
    fun provideRepoNetwork(newsService: NewsService): NewsRepo {
        return NewsRepoImpl(newsService)
    }
}
