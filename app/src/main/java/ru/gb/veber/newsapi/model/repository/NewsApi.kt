package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.gb.veber.newsapi.model.data.Articles
import ru.gb.veber.newsapi.model.data.SourcesRequest
import ru.gb.veber.newsapi.utils.EVERYTHING
import ru.gb.veber.newsapi.utils.TOP_HEADLINES
import ru.gb.veber.newsapi.utils.TOP_HEADLINES_SOURCES

interface NewsApi {
    @GET(TOP_HEADLINES)
    fun getTopicalHeadlines(
        @Query("country") country: String,
        @Query("category") category: String,
    ): Single<Articles>

    @GET(EVERYTHING)
    fun getEverything(
        @Query("q") q: String,
    ): Call<Articles>

    @GET(TOP_HEADLINES_SOURCES)
    fun getSources(): Single<SourcesRequest>

}
