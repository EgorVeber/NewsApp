package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.data.ArticlesDTO
import ru.gb.veber.newsapi.model.data.SourcesRequestDTO
import ru.gb.veber.newsapi.utils.subscribeDefault

class NewsRepoImpl(private val newsApi: NewsApi) : NewsRepo {





    //TOP_HEADLINES
    override fun getTopicalHeadlinesCountryCategoryKeyword(
        country: String,
        category: String,
        keyWord: String,
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesCountryCategoryKeyword(country, category, keyWord)
            .subscribeDefault()


    override fun getTopicalHeadlinesCountryKeyWord(
        country: String,
        keyWord: String,
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesCountryKeyWord(country, keyWord).subscribeDefault()


    override fun getTopicalHeadlinesCountryCategory(
        country: String,
        category: String,
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesCountryCategory(country, category).subscribeDefault()


    override fun getTopicalHeadlinesCategoryKeyWord(
        category: String,
        keyWord: String,
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesCategoryKeyWord(category, keyWord).subscribeDefault()


    override fun getTopicalHeadlinesSourcesKeyWord(
        keyWord: String,
        sources: String,
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesSourcesKeyWord(keyWord, sources).subscribeDefault()


    override fun getTopicalHeadlinesCountry(country: String): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesCountry(country).subscribeDefault()

    override fun getTopicalHeadlinesCategory(category: String): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesCategory(category).subscribeDefault()

    override fun getTopicalHeadlinesKeyWord(keyWord: String): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesKeyWord(keyWord).subscribeDefault()

    override fun getTopicalHeadlinesSources(sources: String): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesSources(sources).subscribeDefault()


    //Everything
    override fun getEverythingKeyWordSearchIn(
        q: String,
        searchIn: String?,
        language: String?,
        sortBy: String?,
        from: String?,
        to: String?,
    ): Single<ArticlesDTO> =
        newsApi.getEverythingKeyWordSearchIn(q, searchIn, language, sortBy, from, to)
            .subscribeDefault()


    override fun getEverythingKeyWordSearchInSources(
        sources: String,
        q: String?,
        searchIn: String?,
        sortBy: String?,
        from: String?,
        to: String?,
    ): Single<ArticlesDTO> =
        newsApi.getEverythingKeyWordSearchInSources(sources, q, searchIn, sortBy, from, to)
            .subscribeDefault()

    //TOP_HEADLINES_SOURCES
    override fun getSources(
        category: String?,
        language: String?,
        country: String?,
    ): Single<SourcesRequestDTO> =
        newsApi.getSources(category, language, country).subscribeDefault()
}