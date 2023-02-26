package ru.gb.veber.newsapi.model.network

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.gb.veber.newsapi.utils.EVERYTHING
import ru.gb.veber.newsapi.utils.TOP_HEADLINES
import ru.gb.veber.newsapi.utils.TOP_HEADLINES_SOURCES

interface NewsApi {
    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesCountryCategoryKeyword(
        @Query("country") country: String?,
        @Query("category") category: String?,
        @Query("q") keyWord: String,
        @Query("apiKey") key: String,
    ): Single<ArticlesDTO>

    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesCategoryCountry(
        @Query("category") category: String,
        @Query("country") country: String?,
        @Query("apiKey") key: String,
    ): Single<ArticlesDTO>

    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesSourcesKeyWord(
        @Query("q") keyWord: String,
        @Query("sources") sources: String,
        @Query("apiKey") key: String,
    ): Single<ArticlesDTO>

    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesSources(
        @Query("sources") sources: String,
        @Query("apiKey") key: String,
    ): Single<ArticlesDTO>

    //EVERYTHING domains прикрутить как нибудь
    @GET(EVERYTHING)
    fun getEverythingKeyWordSearchIn(
        @Query("q") q: String,
        @Query("searchIn") searchIn: String? = null,
        @Query("language") language: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("apiKey") key: String,
    ): Single<ArticlesDTO>

    @GET(EVERYTHING)
    fun getEverythingKeyWordSearchInSources(
        @Query("sources") sources: String? = null,
        @Query("q") q: String? = null,
        @Query("searchIn") searchIn: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("apiKey") key: String,
    ): Single<ArticlesDTO>


    //TOP_HEADLINES_SOURCES
    @GET(TOP_HEADLINES_SOURCES)
    fun getSources(
        @Query("category") category: String? = null,
        @Query("language") language: String? = null,
        @Query("country") country: String? = null,
        @Query("apiKey") key: String? = null,
    ): Single<SourcesRequestDTO>


    @GET(TOP_HEADLINES)
    suspend fun getTopicalHeadlinesCountryCategoryKeywordV2(
        @Query("country") country: String?,
        @Query("category") category: String?,
        @Query("q") keyWord: String,
        @Query("apiKey") key: String,
    ): ArticlesDTO

    @GET(TOP_HEADLINES)
    suspend fun getTopicalHeadlinesCategoryCountryV2(
        @Query("category") category: String,
        @Query("country") country: String?,
        @Query("apiKey") key: String,
    ): ArticlesDTO

    @GET(TOP_HEADLINES)
    suspend fun getTopicalHeadlinesSourcesKeyWordV2(
        @Query("q") keyWord: String,
        @Query("sources") sources: String,
        @Query("apiKey") key: String,
    ): ArticlesDTO

    @GET(TOP_HEADLINES)
    suspend fun getTopicalHeadlinesSourcesV2(
        @Query("sources") sources: String,
        @Query("apiKey") key: String,
    ): ArticlesDTO

    //EVERYTHING domains прикрутить как нибудь
    @GET(EVERYTHING)
    suspend fun getEverythingKeyWordSearchInV2(
        @Query("q") q: String,
        @Query("searchIn") searchIn: String? = null,
        @Query("language") language: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("apiKey") key: String,
    ): ArticlesDTO

    @GET(EVERYTHING)
    suspend fun getEverythingKeyWordSearchInSourcesV2(
        @Query("sources") sources: String? = null,
        @Query("q") q: String? = null,
        @Query("searchIn") searchIn: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("apiKey") key: String,
    ): ArticlesDTO


    //TOP_HEADLINES_SOURCES
    @GET(TOP_HEADLINES_SOURCES)
    suspend fun getSourcesV2(
        @Query("category") category: String? = null,
        @Query("language") language: String? = null,
        @Query("country") country: String? = null,
        @Query("apiKey") key: String? = null,
    ): SourcesRequestDTO
}
