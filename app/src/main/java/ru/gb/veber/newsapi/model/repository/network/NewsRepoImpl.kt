package ru.gb.veber.newsapi.model.repository.network

import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.network.ArticlesDTO
import ru.gb.veber.newsapi.model.network.NewsApi
import ru.gb.veber.newsapi.model.network.SourcesRequestDTO
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

    override fun getTopicalHeadlinesCategoryCountry(
        category: String,
        country: String?,
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesCategoryCountry(category, country).subscribeDefault()


    override fun getTopicalHeadlinesSourcesKeyWord(
        keyWord: String,
        sources: String,
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesSourcesKeyWord(keyWord, sources).subscribeDefault()


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
        sources: String?,
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