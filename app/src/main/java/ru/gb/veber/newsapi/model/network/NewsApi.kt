package ru.gb.veber.newsapi.model.network

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.gb.veber.newsapi.model.ArticlesDTO
import ru.gb.veber.newsapi.model.SourcesRequestDTO
import ru.gb.veber.newsapi.utils.EVERYTHING
import ru.gb.veber.newsapi.utils.TOP_HEADLINES
import ru.gb.veber.newsapi.utils.TOP_HEADLINES_SOURCES

interface NewsApi {
    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesCountryCategoryKeyword(
        @Query("country") country: String?,
        @Query("category") category: String?,
        @Query("q") keyWord: String,
    ): Single<ArticlesDTO>

    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesCategoryCountry(
        @Query("category") category: String,
        @Query("country") country: String?,
    ): Single<ArticlesDTO>

    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesSourcesKeyWord(
        @Query("q") keyWord: String,
        @Query("sources") sources: String,
    ): Single<ArticlesDTO>

    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesSources(
        @Query("sources") sources: String,
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
    ): Single<ArticlesDTO>

    @GET(EVERYTHING)
    fun getEverythingKeyWordSearchInSources(
        @Query("sources") sources: String? = null,
        @Query("q") q: String? = null,
        @Query("searchIn") searchIn: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
    ): Single<ArticlesDTO>


    //TOP_HEADLINES_SOURCES
    @GET(TOP_HEADLINES_SOURCES)
    fun getSources(
        @Query("category") category: String? = null,
        @Query("language") language: String? = null,
        @Query("country") country: String? = null,
    ): Single<SourcesRequestDTO>
}
