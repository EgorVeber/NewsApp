package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import ru.gb.veber.newsapi.model.Articles
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.SourcesRequest
import ru.gb.veber.newsapi.utils.EVERYTHING
import ru.gb.veber.newsapi.utils.TOP_HEADLINES
import ru.gb.veber.newsapi.utils.TOP_HEADLINES_SOURCES

interface NewsApi {
    @GET(TOP_HEADLINES)
    fun getTopicalHeadlines(
        @Header("X-Api-Key") key: String,
        @Query("country") country: String,
        @Query("category") category: String,
    ): Call<Articles>

    @GET(EVERYTHING)
    fun getEverything(
        @Header("X-Api-Key") key: String,
        @Query("q") q: String,
    ): Call<Articles>

    @GET(TOP_HEADLINES_SOURCES)
    fun getSources(@Header("X-Api-Key") key: String): Single<SourcesRequest>

    @GET(TOP_HEADLINES_SOURCES)
    fun getSources2(@Header("X-Api-Key") key: String): Call<SourcesRequest>
}
