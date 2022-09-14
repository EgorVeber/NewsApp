package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.data.Articles
import ru.gb.veber.newsapi.model.data.SourcesRequest
import ru.gb.veber.newsapi.utils.subscribeDefault

class NewsRepoImpl(private val newsApi: NewsApi) : NewsRepo {

    override fun getSources(): Single<SourcesRequest> =
        newsApi.getSources()
            .subscribeDefault()

    override fun getTopicalHeadlinesCountryCategoryKeyword(
        country: String,
        category: String,
        keyWord: String,
    ): Single<Articles> =
        newsApi.getTopicalHeadlinesCountryCategoryKeyword(country, category, keyWord)
            .subscribeDefault()


    override fun getTopicalHeadlinesCountryKeyWord(
        country: String,
        keyWord: String,
    ): Single<Articles> =
        newsApi.getTopicalHeadlinesCountryKeyWord(country, keyWord).subscribeDefault()


    override fun getTopicalHeadlinesCountryCategory(
        country: String,
        category: String,
    ): Single<Articles> =
        newsApi.getTopicalHeadlinesCountryCategory(country, category).subscribeDefault()


    override fun getTopicalHeadlinesCategoryKeyWord(
        category: String,
        keyWord: String,
    ): Single<Articles> =
        newsApi.getTopicalHeadlinesCategoryKeyWord(category, keyWord).subscribeDefault()


    override fun getTopicalHeadlinesSourcesKeyWord(
        keyWord: String,
        sources: String,
    ): Single<Articles> =
        newsApi.getTopicalHeadlinesSourcesKeyWord(keyWord, sources).subscribeDefault()


    override fun getTopicalHeadlinesCountry(country: String): Single<Articles> =
        newsApi.getTopicalHeadlinesCountry(country).subscribeDefault()

    override fun getTopicalHeadlinesCategory(category: String): Single<Articles> =
        newsApi.getTopicalHeadlinesCategory(category).subscribeDefault()

    override fun getTopicalHeadlinesKeyWord(keyWord: String): Single<Articles> =
        newsApi.getTopicalHeadlinesKeyWord(keyWord).subscribeDefault()

    override fun getTopicalHeadlinesSources(sources: String): Single<Articles> =
        newsApi.getTopicalHeadlinesSources(sources).subscribeDefault()

    override fun getEverything(keyWord: String): Single<Articles> =
        newsApi.getEverything(keyWord).subscribeDefault()
}