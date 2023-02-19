package ru.gb.veber.newsapi.model.repository.network

import android.util.Log
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
        key: String
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesCountryCategoryKeyword(country, category, keyWord, key)
            .subscribeDefault()

    override fun getTopicalHeadlinesCategoryCountry(
        category: String,
        country: String?,
        key: String
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesCategoryCountry(category, country, key).subscribeDefault()


    override fun getTopicalHeadlinesSourcesKeyWord(
        keyWord: String,
        sources: String,
        key: String
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
        key: String
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
        key: String
    ): Single<ArticlesDTO> =
        newsApi.getEverythingKeyWordSearchInSources(sources, q, searchIn, sortBy, from, to, key)
            .subscribeDefault()

    //TOP_HEADLINES_SOURCES
    override fun getSources(
        category: String?,
        language: String?,
        country: String?,
        key: String
    ): Single<SourcesRequestDTO> =
        newsApi.getSources(category, language, country, key).subscribeDefault()
}