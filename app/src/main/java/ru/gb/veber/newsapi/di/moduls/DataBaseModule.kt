package ru.gb.veber.newsapi.di.moduls

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.gb.veber.newsapi.model.database.NewsDataBase
import ru.gb.veber.newsapi.model.repository.room.*
import javax.inject.Singleton

@Module
object DataBaseModule {

    @Singleton
    @Provides
    fun database(context: Context): NewsDataBase =
        NewsDataBase.createDb(context)

    @Singleton
    @Provides
    fun provideAccountsDao(database: NewsDataBase): AccountRepo =
        AccountRepoImpl(database.accountsDao())

    @Singleton
    @Provides
    fun provideArticleDao(database: NewsDataBase): ArticleRepo =
        ArticleRepoImpl(database.articleDao())

    @Singleton
    @Provides
    fun provideSourcesDao(database: NewsDataBase): SourcesRepo =
        SourcesRepoImpl(database.sourcesDao())

    @Singleton
    @Provides
    fun provideAccountSourcesDao(database: NewsDataBase): AccountSourcesRepo =
        AccountSourcesRepoImpl(database.accountSourcesDao())

    @Singleton
    @Provides
    fun provideHistorySelectDao(database: NewsDataBase): HistorySelectRepo =
        HistorySelectRepoImpl(database.historySelectDao())

    @Singleton
    @Provides
    fun provideCountryDao(database: NewsDataBase): CountryRepo =
        CountryRepoImpl(database.countryDao())
}