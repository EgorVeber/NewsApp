package ru.gb.veber.newsapi.model.repository.network

import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.network.ArticlesDTO
import ru.gb.veber.newsapi.model.network.NewsApi
import ru.gb.veber.newsapi.model.network.SourcesRequestDTO
import ru.gb.veber.newsapi.utils.extentions.subscribeDefault

class NewsRepoImpl(private val newsApi: NewsApi) : NewsRepo {

    //TOP_HEADLINES
    override fun getTopicalHeadlinesCountryCategoryKeyword(
        country: String,
        category: String,
        keyWord: String,
        key: String,
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesCountryCategoryKeyword(country, category, keyWord, key)
            .subscribeDefault()

    override fun getTopicalHeadlinesCategoryCountry(
        category: String,
        country: String?,
        key: String,
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesCategoryCountry(category, country, key).subscribeDefault()


    override fun getTopicalHeadlinesSourcesKeyWord(
        keyWord: String,
        sources: String,
        key: String,
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesSourcesKeyWord(keyWord, sources, key).subscribeDefault()


    override fun getTopicalHeadlinesSources(sources: String, key: String): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesSources(sources, key).subscribeDefault()


    //Everything
    override fun getEverythingKeyWordSearchIn(
        q: String,
        searchIn: String?,
        language: String?,
        sortBy: String?,
        from: String?,
        to: String?,
        key: String,
    ): Single<ArticlesDTO> =
        newsApi.getEverythingKeyWordSearchIn(q, searchIn, language, sortBy, from, to, key)
            .subscribeDefault()


    override fun getEverythingKeyWordSearchInSources(
        sources: String?,
        q: String?,
        searchIn: String?,
        sortBy: String?,
        from: String?,
        to: String?,
        key: String,
    ): Single<ArticlesDTO> =
        newsApi.getEverythingKeyWordSearchInSources(sources, q, searchIn, sortBy, from, to, key)
            .subscribeDefault()

    //TOP_HEADLINES_SOURCES
    override fun getSources(
        category: String?,
        language: String?,
        country: String?,
        key: String,
    ): Single<SourcesRequestDTO> =
        newsApi.getSources(category, language, country, key).subscribeDefault()


    override suspend fun getTopicalHeadlinesCountryCategoryKeywordV2(
        country: String,
        category: String,
        keyWord: String,
        key: String,
    ): ArticlesDTO =
        newsApi.getTopicalHeadlinesCountryCategoryKeywordV2(country, category, keyWord, key)


    override suspend fun getTopicalHeadlinesCategoryCountryV2(
        category: String,
        country: String?,
        key: String,
    ): ArticlesDTO =
        newsApi.getTopicalHeadlinesCategoryCountryV2(category, country, key)


    override suspend fun getTopicalHeadlinesSourcesKeyWordV2(
        keyWord: String,
        sources: String,
        key: String,
    ): ArticlesDTO =
        newsApi.getTopicalHeadlinesSourcesKeyWordV2(keyWord, sources, key)


    override suspend fun getTopicalHeadlinesSourcesV2(sources: String, key: String): ArticlesDTO =
        newsApi.getTopicalHeadlinesSourcesV2(sources, key)

    override suspend fun getEverythingKeyWordSearchInV2(
        q: String,
        searchIn: String?,
        language: String?,
        sortBy: String?,
        from: String?,
        to: String?,
        key: String,
    ): ArticlesDTO =
        newsApi.getEverythingKeyWordSearchInV2(q, searchIn, language, sortBy, from, to, key)


    override suspend fun getEverythingKeyWordSearchInSourcesV2(
        sources: String?,
        q: String?,
        searchIn: String?,
        sortBy: String?,
        from: String?,
        to: String?,
        key: String,
    ): ArticlesDTO =
        newsApi.getEverythingKeyWordSearchInSourcesV2(sources, q, searchIn, sortBy, from, to, key)

    override suspend fun getSourcesV2(
        category: String?,
        language: String?,
        country: String?,
        key: String,
    ): SourcesRequestDTO =
        newsApi.getSourcesV2(category, language, country, key)
}