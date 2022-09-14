package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.gb.veber.newsapi.model.data.Articles
import ru.gb.veber.newsapi.model.data.SourcesRequest
import ru.gb.veber.newsapi.utils.EVERYTHING
import ru.gb.veber.newsapi.utils.TOP_HEADLINES
import ru.gb.veber.newsapi.utils.TOP_HEADLINES_SOURCES

interface NewsApi {
    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesCountryCategoryKeyword(
        @Query("country") country: String,
        @Query("category") category: String,
        @Query("q") keyWord: String,
    ): Single<Articles>

    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesCategoryKeyWord(
        @Query("category") category: String,
        @Query("q") keyWord: String,
    ): Single<Articles>

    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesCountryKeyWord(
        @Query("country") country: String,
        @Query("q") keyWord: String,
    ): Single<Articles>

    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesCountryCategory(
        @Query("country") country: String,
        @Query("category") category: String,
    ): Single<Articles>

    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesCountry(
        @Query("country") country: String,
    ): Single<Articles>

    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesCategory(
        @Query("category") category: String,
    ): Single<Articles>

    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesSourcesKeyWord(
        @Query("q") keyWord: String,
        @Query("sources") sources: String,
    ): Single<Articles>

    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesKeyWord(
        @Query("q") keyWord: String,
    ): Single<Articles>

    @GET(TOP_HEADLINES)
    fun getTopicalHeadlinesSources(
        @Query("sources") sources: String,
    ): Single<Articles>


    @GET(TOP_HEADLINES_SOURCES)
    fun getSources(): Single<SourcesRequest>


    @GET(EVERYTHING)
    fun getEverything(
        @Query("q") q: String,
    ): Single<Articles>

}
