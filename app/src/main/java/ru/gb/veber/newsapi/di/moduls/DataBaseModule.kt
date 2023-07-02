package ru.gb.veber.newsapi.di.moduls

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.gb.veber.newsapi.data.database.NewsDataBase
import ru.gb.veber.newsapi.data.repository.AccountRepoImpl
import ru.gb.veber.newsapi.data.repository.AccountSourcesRepoImpl
import ru.gb.veber.newsapi.data.repository.ApiKeysRepositoryImpl
import ru.gb.veber.newsapi.data.repository.ArticleRepoImpl
import ru.gb.veber.newsapi.data.repository.CountryRepoImpl
import ru.gb.veber.newsapi.data.repository.HistorySelectRepoImpl
import ru.gb.veber.newsapi.data.repository.SourcesRepoImpl
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.AccountSourcesRepo
import ru.gb.veber.newsapi.domain.repository.ApiKeysRepository
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import ru.gb.veber.newsapi.domain.repository.CountryRepo
import ru.gb.veber.newsapi.domain.repository.HistorySelectRepo
import ru.gb.veber.newsapi.domain.repository.SourcesRepo
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

    @Singleton
    @Provides
    fun provideApiKeysDao(database: NewsDataBase): ApiKeysRepository =
        ApiKeysRepositoryImpl(database.apiKeysDao())
}
