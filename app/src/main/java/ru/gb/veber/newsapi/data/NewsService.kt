package ru.gb.veber.newsapi.data

import retrofit2.http.GET
import retrofit2.http.Query
import ru.gb.veber.newsapi.common.utils.EVERYTHING
import ru.gb.veber.newsapi.common.utils.TOP_HEADLINES
import ru.gb.veber.newsapi.common.utils.TOP_HEADLINES_SOURCES
import ru.gb.veber.newsapi.data.models.ArticlesBaseResponse
import ru.gb.veber.newsapi.data.models.SourcesBaseResponse

//TODO Разделить на несколько сервисов
interface NewsService {

    @GET(TOP_HEADLINES)
    suspend fun getTopicalHeadlinesCountryCategoryKeywordV2(
        @Query("country") country: String?,
        @Query("category") category: String?,
        @Query("q") keyWord: String,
        @Query("apiKey") key: String,
    ): ArticlesBaseResponse

    @GET(TOP_HEADLINES)
    suspend fun getTopicalHeadlinesCategoryCountryV2(
        @Query("category") category: String,
        @Query("country") country: String?,
        @Query("apiKey") key: String,
    ): ArticlesBaseResponse

    @GET(TOP_HEADLINES)
    suspend fun getTopicalHeadlinesSourcesKeyWordV2(
        @Query("q") keyWord: String,
        @Query("sources") sources: String,
        @Query("apiKey") key: String,
    ): ArticlesBaseResponse

    @GET(TOP_HEADLINES)
    suspend fun getTopicalHeadlinesSourcesV2(
        @Query("sources") sources: String,
        @Query("apiKey") key: String,
    ): ArticlesBaseResponse

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
    ): ArticlesBaseResponse

    @GET(EVERYTHING)
    suspend fun getEverythingKeyWordSearchInSourcesV2(
        @Query("sources") sources: String? = null,
        @Query("q") q: String? = null,
        @Query("searchIn") searchIn: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("apiKey") key: String,
    ): ArticlesBaseResponse


    //TOP_HEADLINES_SOURCES
    @GET(TOP_HEADLINES_SOURCES)
    suspend fun getSourcesV2(
        @Query("category") category: String? = null,
        @Query("language") language: String? = null,
        @Query("country") country: String? = null,
        @Query("apiKey") key: String? = null,
    ): SourcesBaseResponse
}
